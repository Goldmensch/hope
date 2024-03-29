/*
 *     Hope - A minecraft server reimplementation
 *     Copyright (C) 2023 Nick Hensel and contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.madethoughts.hope;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ReflectionAccessFilter;
import io.github.madethoughts.hope.configuration.ServerConfig;
import io.github.madethoughts.hope.configuration.ServerConfig$Implementation;
import io.github.madethoughts.hope.json.deserializers.PlayerProfileDeserializer;
import io.github.madethoughts.hope.json.serializers.ComponentGsonTypeAdapter;
import io.github.madethoughts.hope.json.serializers.StatusResponseSerializer;
import io.github.madethoughts.hope.network.Gatekeeper;
import io.github.madethoughts.hope.network.packets.clientbound.status.StatusResponse;
import io.github.madethoughts.hope.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.Toml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class Server implements AutoCloseable, Runnable {

    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static final GsonComponentSerializer GSON_COMPONENT_SERIALIZER = GsonComponentSerializer.gson();

    public static final Gson GSON = new GsonBuilder()
            .addReflectionAccessFilter(ReflectionAccessFilter.BLOCK_ALL_PLATFORM)
            // -- serializer --
            .registerTypeHierarchyAdapter(Component.class, new ComponentGsonTypeAdapter())
            // register status response serializers
            .registerTypeAdapter(
                    StatusResponseSerializer.VersionSerializer.class,
                    new StatusResponseSerializer.VersionSerializer()
            )
            .registerTypeAdapter(
                    StatusResponseSerializer.PlayersSerializer.class,
                    new StatusResponseSerializer.PlayersSerializer()
            )
            .registerTypeAdapter(StatusResponse.class, new StatusResponseSerializer())

            // -- deserializer --
            .registerTypeAdapter(PlayerProfile.class, new PlayerProfileDeserializer())
            .create();
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final ServerConfig config;
    private final Gatekeeper gatekeeper;

    public Server(ServerConfig config, Gatekeeper gatekeeper) {
        this.config = config;
        this.gatekeeper = gatekeeper;
    }

    /**
     * @return a new running server or null if an expected error occurred and got logged
     * @throws IOException any I/O Exception
     */
    public static Server setup() throws Exception {
        // read and parse configuration
        var configPath = Path.of("config.toml");
        var serverConfig = new ServerConfig$Implementation(MINI_MESSAGE);
        if (Files.notExists(configPath)) {
            Files.writeString(configPath, "version = %s".formatted(serverConfig.defaultVersion()),
                    StandardOpenOption.CREATE_NEW
            );
            log.info("Created new default config.toml");
        }
        var configParsingResult = Toml.parse(configPath);
        if (configParsingResult.hasErrors()) {
            log.error("Parsing of config.toml failed, due to: {}", configParsingResult.errors());
            return null;
        }

        // create server config
        serverConfig.load(configParsingResult);
        switch (serverConfig.checkVersion()) {
            case OUTDATED -> {
                log.error("The config.toml is outdated! Please update the config and increment the version!");
                return null;
            }
            case INVALID -> {
                log.error("The config.toml version is invalid or missing. Please correct it!");
                return null;
            }
            case UP_TO_DATE -> {}
        }

        var gatekeeper = Gatekeeper.open(serverConfig);
        return new Server(serverConfig, gatekeeper);
    }

    @Override
    public void run() {
        try {
            // start listening for packets
            Thread.startVirtualThread(() -> gatekeeper.accept(this))
                  .setName("Gatekeeper");

            // just hold the server alive (development)
            while (true) Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            gatekeeper.close();
        } catch (Exception e) {
            log.error("Unexpected exception while shutting down server, potentially resulting in data loss.");
        }
    }
}
