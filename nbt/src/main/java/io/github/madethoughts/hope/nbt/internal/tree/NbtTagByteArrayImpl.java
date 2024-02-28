package io.github.madethoughts.hope.nbt.internal.tree;

import io.github.madethoughts.hope.nbt.tree.NbtTagByteArray;

import java.lang.foreign.MemorySegment;
import java.util.Arrays;

public record NbtTagByteArrayImpl(
        byte[] value
) implements NbtTagByteArray {

    public byte[] valueUnsafe() {
        return value;
    }

    @Override
    public byte[] value() {
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public MemorySegment segment() {
        return MemorySegment.ofArray(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NbtTagByteArrayImpl other && Arrays.equals(other.value, value);
    }

    @Override
    public String toString() {
        return "NbtTagByteArrayImpl[" +
                "value=" + Arrays.toString(value) +
                ']';
    }
}
