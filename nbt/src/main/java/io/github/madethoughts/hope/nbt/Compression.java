package io.github.madethoughts.hope.nbt;

import java.util.Objects;

public record Compression(
        CompressionType type,
        int bufferSize
) {

    public static final Compression NONE = new Compression(CompressionType.NONE);
    public static final Compression GZIP = new Compression(CompressionType.GZIP);
    public static final Compression ZLIB = new Compression(CompressionType.ZLIB);

    public Compression {
        Objects.requireNonNull(type);
    }

    public Compression(CompressionType type) {
        this(type, -1);
    }

    public boolean bufferSizeSet() {
        return bufferSize >= 0;
    }
}
