package io.github.madethoughts.hope.nbt.deserialization;

import io.github.madethoughts.hope.nbt.Compression;
import io.github.madethoughts.hope.nbt.Mode;
import io.github.madethoughts.hope.nbt.TagType;
import io.github.madethoughts.hope.nbt.tree.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

public final class NbtDeserializer {
    private final Mode mode;
    private final DataInput in;

    private NbtDeserializer(InputStream inputStream, Mode mode, Compression compression) {
        try {
            Objects.requireNonNull(inputStream);
            Objects.requireNonNull(mode);
            Objects.requireNonNull(compression);

            if (compression != Compression.NONE) {
                inputStream = switch (compression.type()) {
                    case GZIP -> new GZIPInputStream(inputStream);
                    case ZLIB -> new DeflaterInputStream(inputStream);
                };

                inputStream = compression.bufferSizeSet()
                        ? new BufferedInputStream(inputStream, compression.bufferSize())
                        : new BufferedInputStream(inputStream);
            }
            this.in = new DataInputStream(inputStream);

            this.mode = mode;
        } catch (IOException e) {
            throw wrappedError(e);
        }
    }
    public static RootCompound deserialize(InputStream inputStream, Mode mode, Compression compression) {
        return new NbtDeserializer(inputStream, mode, compression).deserializeBytes();
    }

    public static RootCompound deserialize(InputStream inputStream, Mode mode) {
        return new NbtDeserializer(inputStream, mode, Compression.NONE).deserializeBytes();
    }

    private static NBTDeserializationException wrappedError(Exception e) {
        return new NBTDeserializationException("Unexpected error during tree processing.", e);
    }

    private NBTDeserializationException error(String msg) {
        return new NBTDeserializationException(-1, msg);
    }

    private RootCompound deserializeBytes() {
        try {
            if (readType() != TagType.COMPOUND) throw error("NBT Data has to start with a compound");

            String name = switch (mode) {
                case NETWORK -> "";
                case FILE -> unwrappedString();
            };

            var payload = compound();
            return new RootCompound(name, payload);
        } catch (IOException e) {
            throw wrappedError(e);
        }
    }

    private NbtTag deserialize(TagType type) throws IOException {
        return switch (type) {
            case END -> throw error("TAG_END isn't allowed to be wrapped in a named tag");
            case BYTE -> new NbtTagByte(in.readByte());
            case SHORT -> new NbtTagShort(in.readShort());
            case INT -> new NbtTagInt(in.readInt());
            case LONG -> new NbtTagLong(in.readLong());
            case FLOAT -> new NbtTagFloat(in.readFloat());
            case DOUBLE -> new NbtTagDouble(in.readDouble());
            case STRING -> new NbtTagString(unwrappedString());
            case LIST -> list();
            case COMPOUND -> compound();
            case BYTE_ARRAY -> {
                int length = in.readInt();
                var bytes = new byte[length];
                in.readFully(bytes);
                yield new NbtTagByteArray(bytes);
            }
            case INT_ARRAY -> {
                int length = in.readInt();
                var bytes = new byte[length * Integer.BYTES];
                in.readFully(bytes);
                yield new NbtTagIntArray(ByteBuffer.wrap(bytes).asIntBuffer().array());
            }
            case LONG_ARRAY -> {
                int length = in.readInt();
                var bytes = new byte[length * Long.BYTES];
                in.readFully(bytes);
                yield new NbtTagLongArray(ByteBuffer.wrap(bytes).asLongBuffer().array());
            }
        };
    }

    private TagType readType() throws IOException {
        return TagType.byId(in.readByte());
    }

    // faster than DataInput#readUTF
    private String unwrappedString() throws IOException {
        // unsigned short
        int length = in.readUnsignedShort();
        var bytes = new byte[length];
        in.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private NbtTagCompound compound() throws IOException {
        var tags = new HashMap<String, NbtTag>();
        while (true) {
            TagType type = readType();
            if (type == TagType.END) break;
            String name = unwrappedString();
            NbtTag payload = deserialize(type);

            tags.put(name, payload);
        }
        return new NbtTagCompound(tags);
    }

    private NbtTagList<?> list() throws IOException {
        var type = readType();
        int length = in.readInt();
        var tags = new ArrayList<NbtTag>(length);
        for (int i = 0; i < length; i++) {
            tags.add(deserialize(type));
        }
        return new NbtTagList<>(tags);
    }
}
