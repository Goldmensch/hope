package nbt;

import io.github.madethoughts.hope.nbt.Mode;
import io.github.madethoughts.hope.nbt.deserialization.NbtDeserializer;
import io.github.madethoughts.hope.nbt.tree.NbtRootCompound;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings("unused")
public class DeserializerBenchmark {

    @State(Scope.Benchmark)
    public static class DesState {
        public byte[] registryDataPackBytes;
        public byte[] compressedRegistryDataPackBytes;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            this.registryDataPackBytes = Files.readAllBytes(Path.of("src/jmh/resources/sample.nbt"));
            try(var stream = new FastByteArrayOutputStream()) {
                try(var gzipStream = new GZIPOutputStream(stream)) {
                    gzipStream.write(registryDataPackBytes);
                    gzipStream.finish();
                }
                this.compressedRegistryDataPackBytes = stream.array;
            }
        }
    }

    @Benchmark
    public NbtRootCompound hopeNbt(DesState state) {
        return NbtDeserializer.deserialize(new FastByteArrayInputStream(state.registryDataPackBytes), Mode.FILE);
    }

    @Benchmark
    public NBT hephaistosNbt(DesState state) throws NBTException, IOException {
        try(var reader = new org.jglrxavpok.hephaistos.nbt.NBTReader(new FastByteArrayInputStream(state.registryDataPackBytes))) {
            return reader.read();
        }
    }

    @Benchmark
    public NBTCompound nedit(DesState state) throws IOException {
        return NBTReader.read(new FastByteArrayInputStream(state.registryDataPackBytes));
    }

    @Benchmark
    public CompoundBinaryTag adventure(DesState state) throws IOException {
        return BinaryTagIO.reader().read(new FastByteArrayInputStream(state.registryDataPackBytes));
    }
}
