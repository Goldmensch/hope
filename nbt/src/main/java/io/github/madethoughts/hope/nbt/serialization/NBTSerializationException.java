package io.github.madethoughts.hope.nbt.serialization;

public final class NBTSerializationException extends RuntimeException {
    public NBTSerializationException(String error, Object... args) {
        super("Serialization exception: %s".formatted(error.formatted(args)));
    }

    public NBTSerializationException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
