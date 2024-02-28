package nbt;

import io.github.madethoughts.hope.nbt.Mode;
import io.github.madethoughts.hope.nbt.deserialization.NbtDeserializer;
import io.github.madethoughts.hope.nbt.serialization.NbtSerializer;
import io.github.madethoughts.hope.nbt.tree.NbtRootCompound;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.NBTWriter;
import me.nullicorn.nedit.type.NBTCompound;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class SerializerBenchmark {

    @State(Scope.Benchmark)
    public static class SerState {
        public NbtRootCompound hopeNbt;
        public NBTCompound neditNbt;
        public CompoundBinaryTag adventureNbt;

        public NBT hepahistosNbt;

        @Setup
        public void setup() throws IOException, NBTException {
            var registryDataPackBytes = Files.readAllBytes(Path.of("src/jmh/resources/sample.nbt"));
            hopeNbt = NbtDeserializer.deserialize(new FastByteArrayInputStream(registryDataPackBytes), Mode.FILE);
            neditNbt = NBTReader.read(new FastByteArrayInputStream(registryDataPackBytes));
            adventureNbt = BinaryTagIO.reader().read(new FastByteArrayInputStream(registryDataPackBytes));
            hepahistosNbt = new org.jglrxavpok.hephaistos.nbt.NBTReader(new FastByteArrayInputStream(registryDataPackBytes)).read();
        }
    }

    @Benchmark
    public byte[] hopeNbt(SerState state) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        NbtSerializer.serialize(outputStream, state.hopeNbt, Mode.FILE);
        return outputStream.array;
    }

    @Benchmark
    public byte[] hephaistosNbt(SerState state) throws IOException {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        try(var writer = new org.jglrxavpok.hephaistos.nbt.NBTWriter(outputStream)) {
            writer.writeRaw(state.hepahistosNbt);
        }
        return outputStream.array;
    }

    @Benchmark
    public byte[] nedit(SerState state) throws IOException {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        NBTWriter.write(state.neditNbt, outputStream, false);
        return outputStream.array;
    }

    @Benchmark
    public byte[] adventureNbt(SerState state) throws IOException {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        BinaryTagIO.writer().write(state.adventureNbt, outputStream);
        return outputStream.array;
    }
}
