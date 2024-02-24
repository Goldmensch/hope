package io.github.madethoughts.hope.nbt.serialization;

import io.github.madethoughts.hope.nbt.Compression;
import io.github.madethoughts.hope.nbt.Mode;
import io.github.madethoughts.hope.nbt.TagType;
import io.github.madethoughts.hope.nbt.tree.*;

import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import static java.util.FormatProcessor.FMT;

public final class NbtSerializer {
    private final RootCompound tree;
    private final Mode mode;
    private final DataOutput out;

    private NbtSerializer(OutputStream outputStream, RootCompound tree, Mode mode, Compression compression) {
        try {
            Objects.requireNonNull(outputStream);
            Objects.requireNonNull(tree);
            Objects.requireNonNull(mode);
            Objects.requireNonNull(compression);

            if (compression != Compression.NONE) {
                outputStream = switch (compression.type()) {
                    case GZIP -> new GZIPOutputStream(outputStream);
                    case ZLIB -> new DeflaterOutputStream(outputStream);
                };

                outputStream = compression.bufferSizeSet()
                        ? new BufferedOutputStream(outputStream, compression.bufferSize())
                        : new BufferedOutputStream(outputStream);
            }
            this.out = new FastDataOutputStream(outputStream);

            this.tree = tree;
            this.mode = mode;
        } catch (IOException e) {
            throw wrappedError(e);
        }
    }

    public static void serialize(OutputStream outputStream, RootCompound tree, Mode mode, Compression compression) {
        new NbtSerializer(outputStream, tree, mode, compression).serializeTree();
    }

    public static void serialize(OutputStream outputStream, RootCompound tree, Mode mode) {
        new NbtSerializer(outputStream, tree, mode, Compression.NONE).serializeTree();
    }

    private static NBTSerializationException wrappedError(Exception e) {
        return new NBTSerializationException("Unexpected error during tree processing.", e);
    }

    private NBTSerializationException errorArrayToBig() {
        return new NBTSerializationException("array is to big");
    }

    private void serializeTree() {
        try {
            switch (mode) {
                case NETWORK -> out.write(TagType.COMPOUND.id());
                case FILE -> {
                    out.write(TagType.COMPOUND.id());
                    writeString(tree.name());
                }
            }

            write(tree.compound());

            if (out instanceof GZIPOutputStream gzipOutputStream) {
                gzipOutputStream.finish();
            }
        } catch (IOException ioException) {
            throw wrappedError(ioException);
        }
    }

    // faster than DataOutStream#writeUtf
    private void writeString(String value) throws IOException {
        var bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > Short.MAX_VALUE)
            throw new NBTSerializationException(FMT."String length exceed maximum of \{Short.MAX_VALUE}");
        out.writeChar((char) bytes.length);
        out.write(bytes);
    }

    private void write(NbtTag current) {
        try {
            switch (current) {
                case NbtTagCompound(Map<String, NbtTag> tags) -> {
                    for (var entry : tags.entrySet()) {
                        String name = entry.getKey();
                        NbtTag wrappedNbtTag = entry.getValue();

                        out.write(TagType.byTagClass(wrappedNbtTag).id());
                        writeString(name);
                        write(wrappedNbtTag);
                    }

                    out.write(TagType.END.id());
                }
                case NbtTagByte(byte value) -> out.write(value);
                case NbtTagShort(short value) -> out.writeShort(value);
                case NbtTagInt(int value) -> out.writeInt(value);
                case NbtTagLong(long value) -> out.writeLong(value);
                case NbtTagFloat(float value) -> out.writeFloat(value);
                case NbtTagDouble(double value) -> out.writeDouble(value);
                case NbtTagByteArray(byte[] value) -> {
                    out.writeInt(value.length);
                    out.write(value);
                }
                case NbtTagString(String value) -> writeString(value);
                case NbtTagList(List<? extends NbtTag> nbtTags) -> {
                    byte id = (byte) (nbtTags.isEmpty()
                            ? TagType.END.id()
                            : TagType.byTagClass(nbtTags.getFirst()).id());

                    out.write(id);
                    out.writeInt(nbtTags.size());
                    nbtTags.forEach(this::write);
                }
                case NbtTagIntArray(int[] value) -> {
                    if (((long) value.length * Integer.BYTES > Integer.MAX_VALUE)) throw errorArrayToBig();
                    out.writeInt(value.length);

                    ByteBuffer buffer = ByteBuffer.allocate(value.length * Integer.BYTES);
                    buffer.asIntBuffer().put(value);
                    out.write(buffer.array());
                }
                case NbtTagLongArray(long[] value) -> {
                    if (((long) value.length * Long.BYTES > Integer.MAX_VALUE)) throw errorArrayToBig();
                    out.writeInt(value.length);

                    ByteBuffer buffer = ByteBuffer.allocate(value.length * Long.BYTES);
                    buffer.asLongBuffer().put(value);
                    out.write(buffer.array());
                }
            }
        } catch (IOException e) {
            throw wrappedError(e);
        }
    }
}
