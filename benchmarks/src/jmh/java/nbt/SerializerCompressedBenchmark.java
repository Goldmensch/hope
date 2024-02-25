package nbt;

import io.github.madethoughts.hope.nbt.Compression;
import io.github.madethoughts.hope.nbt.CompressionType;
import io.github.madethoughts.hope.nbt.Mode;
import io.github.madethoughts.hope.nbt.deserialization.NbtDeserializer;
import io.github.madethoughts.hope.nbt.serialization.NbtSerializer;
import io.github.madethoughts.hope.nbt.tree.RootCompound;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.NBTWriter;
import me.nullicorn.nedit.type.NBTCompound;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class SerializerCompressedBenchmark {

    @State(Scope.Benchmark)
    public static class SerState {
        public RootCompound hopeNbt;
        public NBTCompound neditNbt;
        public CompoundBinaryTag adventureNbt;

        @Setup
        public void setup() throws IOException {
            var registryDataPackBytes = Files.readAllBytes(Path.of("src/jmh/resources/registry-data-packet.nbt"));
            hopeNbt = NbtDeserializer.deserialize(new FastByteArrayInputStream(registryDataPackBytes), Mode.FILE);
            neditNbt = NBTReader.read(new FastByteArrayInputStream(registryDataPackBytes));
            adventureNbt = BinaryTagIO.reader().read(new FastByteArrayInputStream(registryDataPackBytes));
        }
    }

    @Benchmark
    public byte[] hopeNbtCompressed(SerState state) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        NbtSerializer.serialize(outputStream, state.hopeNbt, Mode.FILE, new Compression(CompressionType.GZIP));
        return outputStream.array;
    }

    @Benchmark
    public byte[] hopeNbtCompressedFastUtil(SerState state) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        NbtSerializer.serialize(outputStream, state.hopeNbt, Mode.FILE, new Compression(CompressionType.GZIP), FastUtilCustomization.INSTANCE);
        return outputStream.array;
    }

    @Benchmark
    public byte[] neditCompressed(SerState state) throws IOException {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        NBTWriter.write(state.neditNbt, outputStream, true);
        return outputStream.array;
    }

    @Benchmark
    public byte[] adventureNbtCompressed(SerState state) throws IOException {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        BinaryTagIO.writer().write(state.adventureNbt, outputStream, BinaryTagIO.Compression.GZIP);
        return outputStream.array;
    }
}
