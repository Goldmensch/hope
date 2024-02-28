package io.github.madethoughts.hope.nbt.serialization;

import io.github.madethoughts.hope.nbt.*;
import io.github.madethoughts.hope.nbt.internal.tree.*;
import io.github.madethoughts.hope.nbt.tree.NbtRootCompound;
import io.github.madethoughts.hope.nbt.tree.NbtTag;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class NbtSerializer {
    private final NbtRootCompound tree;
    private final Mode mode;
    private final DataOutputStream out;

    private NbtSerializer(OutputStream outputStream, NbtRootCompound tree, Mode mode, Compression compression, Customization customization) {
        try {
            Objects.requireNonNull(outputStream);
            Objects.requireNonNull(tree);
            Objects.requireNonNull(mode);
            Objects.requireNonNull(compression);
            Objects.requireNonNull(customization);

            if (compression.type() != CompressionType.NONE) {
                outputStream = switch (compression.type()) {
                    case GZIP -> customization.gzipOutputStream(outputStream);
                    case ZLIB -> customization.zlibOutputStream(outputStream);
                    default -> throw new IllegalStateException("Cannot reach this point");
                };

                outputStream = compression.bufferSizeSet()
                        ? customization.bufferedOutputStream(outputStream, compression.bufferSize())
                        : customization.bufferedOutputStream(outputStream);
            }
            this.out = customization.dataOutput(outputStream);

            this.tree = tree;
            this.mode = mode;
        } catch (IOException e) {
            throw wrappedError(e);
        }
    }

    public static void serialize(OutputStream outputStream, NbtRootCompound tree, Mode mode, Compression compression, Customization customization) {
        new NbtSerializer(outputStream, tree, mode, compression, customization).serializeTree();
    }

    public static void serialize(OutputStream outputStream, NbtRootCompound tree, Mode mode, Compression compression) {
        new NbtSerializer(outputStream, tree, mode, compression, Customization.DEFAULT).serializeTree();
    }

    public static void serialize(OutputStream outputStream, NbtRootCompound tree, Mode mode) {
        new NbtSerializer(outputStream, tree, mode, Compression.NONE, Customization.DEFAULT).serializeTree();
    }

    private static NBTSerializationException wrappedError(Exception e) {
        return new NBTSerializationException("Unexpected error during tree processing.", e);
    }

    private void serializeTree() {
        try (out) {
            switch (mode) {
                case NETWORK -> out.write(TagType.COMPOUND.id());
                case FILE -> {
                    out.write(TagType.COMPOUND.id());
                    out.writeUTF(tree.name());
                }
            }

            write(tree.compound());
        } catch (IOException ioException) {
            throw wrappedError(ioException);
        }
    }

    private void writeBytes(int size, byte[] bytes) throws IOException {
        out.writeInt(size);
        out.write(bytes);
    }

    private void write(NbtTag current) {
        try {
            switch (current) {
                case NbtTagCompoundImpl(Map<String, NbtTag> tags) -> {
                    for (var entry : tags.entrySet()) {
                        String name = entry.getKey();
                        NbtTag wrappedNbtTag = entry.getValue();

                        out.write(TagType.byTagClass(wrappedNbtTag).id());
                        out.writeUTF(name);
                        write(wrappedNbtTag);
                    }

                    out.write(TagType.END.id());
                }
                case NbtTagByteImpl(byte value) -> out.write(value);
                case NbtTagShortImpl(short value) -> out.writeShort(value);
                case NbtTagIntImpl(int value) -> out.writeInt(value);
                case NbtTagLongImpl(long value) -> out.writeLong(value);
                case NbtTagFloatImpl(float value) -> out.writeFloat(value);
                case NbtTagDoubleImpl(double value) -> out.writeDouble(value);
                case NbtTagByteArrayImpl tag -> {
                    byte[] value = tag.valueUnsafe();
                    writeBytes(value.length, value);
                }
                case NbtTagStringImpl(String value) -> out.writeUTF(value);
                case NbtTagListImpl(List<? extends NbtTag> nbtTags) -> {
                    byte id = (byte) (nbtTags.isEmpty()
                            ? TagType.END.id()
                            : TagType.byTagClass(nbtTags.getFirst()).id());

                    out.write(id);
                    out.writeInt(nbtTags.size());
                    nbtTags.forEach(this::write);
                }
                case NbtTagIntArrayImpl tag -> {
                    int[] integers = tag.valueUnsafe();
                    byte[] bytes = new byte[integers.length * Integer.BYTES];
                    MemorySegment.copy(MemorySegment.ofArray(integers), ValueLayout.JAVA_INT_UNALIGNED, 0,
                            MemorySegment.ofArray(bytes), ValueLayout.JAVA_INT_UNALIGNED.withOrder(ByteOrder.BIG_ENDIAN), 0,
                            integers.length);
                    writeBytes(integers.length, bytes);
                }
                case NbtTagLongArrayImpl tag -> {
                    long[] longs = tag.valueUnsafe();
                    byte[] bytes = new byte[longs.length * Long.BYTES];
                    MemorySegment.copy(MemorySegment.ofArray(longs), ValueLayout.JAVA_LONG_UNALIGNED, 0,
                            MemorySegment.ofArray(bytes), ValueLayout.JAVA_LONG_UNALIGNED.withOrder(ByteOrder.BIG_ENDIAN), 0,
                            longs.length);
                    writeBytes(longs.length, bytes);
                }
            }
        } catch (IOException e) {
            throw wrappedError(e);
        }
    }
}
