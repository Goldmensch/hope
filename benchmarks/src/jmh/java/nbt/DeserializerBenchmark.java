package nbt;

import io.github.madethoughts.hope.nbt.Compression;
import io.github.madethoughts.hope.nbt.CompressionType;
import io.github.madethoughts.hope.nbt.Mode;
import io.github.madethoughts.hope.nbt.deserialization.NbtDeserializer;
import io.github.madethoughts.hope.nbt.tree.RootCompound;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
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
            this.registryDataPackBytes = Files.readAllBytes(Path.of("src/jmh/resources/registry-data-packet.nbt"));
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
    public RootCompound hopeNbt(DesState state) {
        return NbtDeserializer.deserialize(new FastByteArrayInputStream(state.registryDataPackBytes), Mode.FILE);
    }

    @Benchmark
    public NBTCompound nedit(DesState state) throws IOException {
        return NBTReader.read(new FastByteArrayInputStream(state.registryDataPackBytes));
    }

    @Benchmark
    public CompoundBinaryTag adventure(DesState state) throws IOException {
        return BinaryTagIO.reader().read(new FastByteArrayInputStream(state.registryDataPackBytes));
    }
    @Benchmark
    public RootCompound hopeNbtCompressed(DesState state) {
        return NbtDeserializer.deserialize(new FastByteArrayInputStream(state.compressedRegistryDataPackBytes), Mode.FILE, new Compression(CompressionType.GZIP));
    }

    @Benchmark
    public RootCompound hopeNbtCompressedFastUtil(DesState state) {
        return NbtDeserializer.deserialize(new FastByteArrayInputStream(state.compressedRegistryDataPackBytes), Mode.FILE, new Compression(CompressionType.GZIP), FastUtilCustomization.INSTANCE);
    }

    @Benchmark
    public NBTCompound neditCompressed(DesState state) throws IOException {
        return NBTReader.read(new FastByteArrayInputStream(state.compressedRegistryDataPackBytes));
    }
    @Benchmark
    public CompoundBinaryTag adventureCompressed(DesState state) throws IOException {
        return BinaryTagIO.reader().read(new FastByteArrayInputStream(state.compressedRegistryDataPackBytes), BinaryTagIO.Compression.GZIP);
    }
}
