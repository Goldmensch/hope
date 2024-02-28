package io.github.madethoughts.hope.nbt.deserialization;

import io.github.madethoughts.hope.nbt.*;
import io.github.madethoughts.hope.nbt.internal.tree.*;
import io.github.madethoughts.hope.nbt.tree.NbtRootCompound;
import io.github.madethoughts.hope.nbt.tree.NbtTag;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public final class NbtDeserializer {
    private final Mode mode;
    private final DataInputStream in;

    private NbtDeserializer(InputStream inputStream, Mode mode, Compression compression, Customization customization) {
        try {
            Objects.requireNonNull(inputStream);
            Objects.requireNonNull(mode);
            Objects.requireNonNull(compression);
            Objects.requireNonNull(customization);

            if (compression.type() != CompressionType.NONE) {
                inputStream = switch (compression.type()) {
                    case GZIP -> customization.gzipInputStream(inputStream);
                    case ZLIB -> customization.zlibInputStream(inputStream);
                    default -> throw new IllegalStateException("Cannot reach this point");
                };

                inputStream = compression.bufferSizeSet()
                        ? customization.bufferedInputStream(inputStream, compression.bufferSize())
                        : customization.bufferedInputStream(inputStream);
            }
            this.in = customization.dataInput(inputStream);

            this.mode = mode;
        } catch (IOException e) {
            throw wrappedError(e);
        }
    }

    public static NbtRootCompound deserialize(InputStream inputStream, Mode mode, Compression compression, Customization customization) {
        return new NbtDeserializer(inputStream, mode, compression, customization).deserializeBytes();
    }

    public static NbtRootCompound deserialize(InputStream inputStream, Mode mode, Compression compression) {
        return new NbtDeserializer(inputStream, mode, compression, Customization.DEFAULT).deserializeBytes();
    }

    public static NbtRootCompound deserialize(InputStream inputStream, Mode mode) {
        return new NbtDeserializer(inputStream, mode, Compression.NONE, Customization.DEFAULT).deserializeBytes();
    }

    private static NBTDeserializationException wrappedError(Exception e) {
        return new NBTDeserializationException("Unexpected error during tree processing.", e);
    }

    private NBTDeserializationException error(String msg) {
        return new NBTDeserializationException(-1, msg);
    }

    private NbtRootCompoundImpl deserializeBytes() {
        try (in) {
            if (readType() != TagType.COMPOUND) throw error("NBT Data has to start with a compound");

            String name = switch (mode) {
                case NETWORK -> "";
                case FILE -> in.readUTF();
            };

            var payload = compound();
            return new NbtRootCompoundImpl(name, payload);
        } catch (IOException e) {
            throw wrappedError(e);
        }
    }

    private byte[] readBytes(int typeSize) throws IOException {
        int length = in.readInt() * typeSize;
        var bytes = new byte[length];
        in.readFully(bytes);
        return bytes;
    }

    private NbtTag deserialize(TagType type) throws IOException {
        return switch (type) {
            case END -> throw error("TAG_END isn't allowed to be wrapped in a named tag");
            case BYTE -> new NbtTagByteImpl(in.readByte());
            case SHORT -> new NbtTagShortImpl(in.readShort());
            case INT -> new NbtTagIntImpl(in.readInt());
            case LONG -> new NbtTagLongImpl(in.readLong());
            case FLOAT -> new NbtTagFloatImpl(in.readFloat());
            case DOUBLE -> new NbtTagDoubleImpl(in.readDouble());
            case STRING -> new NbtTagStringImpl(in.readUTF());
            case LIST -> list();
            case COMPOUND -> compound();
            case BYTE_ARRAY -> new NbtTagByteArrayImpl(readBytes(Byte.BYTES));
            case INT_ARRAY -> {
                byte[] bytes = readBytes(Integer.BYTES);
                int[] integers = new int[bytes.length / Integer.BYTES];
                MemorySegment.copy(MemorySegment.ofArray(bytes), ValueLayout.JAVA_INT_UNALIGNED.withOrder(ByteOrder.BIG_ENDIAN), 0,
                        MemorySegment.ofArray(integers), ValueLayout.OfInt.JAVA_INT_UNALIGNED, 0,
                        integers.length);
                yield new NbtTagIntArrayImpl(integers);
            }
            case LONG_ARRAY -> {
                byte[] bytes = readBytes(Long.BYTES);
                long[] longs = new long[bytes.length / Long.BYTES];
                MemorySegment.copy(MemorySegment.ofArray(bytes), ValueLayout.JAVA_LONG_UNALIGNED.withOrder(ByteOrder.BIG_ENDIAN), 0,
                        MemorySegment.ofArray(longs), ValueLayout.OfInt.JAVA_LONG_UNALIGNED, 0,
                        longs.length);
                yield new NbtTagLongArrayImpl(longs);
            }
        };
    }

    private TagType readType() throws IOException {
        return TagType.byId(in.readByte());
    }

    private NbtTagCompoundImpl compound() throws IOException {
        var tags = new HashMap<String, NbtTag>();
        while (true) {
            TagType type = readType();
            if (type == TagType.END) break;
            String name = in.readUTF();
            NbtTag payload = deserialize(type);

            tags.put(name, payload);
        }
        return new NbtTagCompoundImpl(tags);
    }

    private NbtTagListImpl<?> list() throws IOException {
        var type = readType();
        int length = in.readInt();
        var tags = new ArrayList<NbtTag>(length);
        for (int i = 0; i < length; i++) {
            tags.add(deserialize(type));
        }
        return new NbtTagListImpl<>(tags);
    }
}
