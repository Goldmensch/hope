package io.github.madethoughts.hope.nbt.internal.tree;

import io.github.madethoughts.hope.nbt.tree.NbtTagLongArray;

import java.lang.foreign.MemorySegment;
import java.util.Arrays;

public record NbtTagLongArrayImpl(
        long[] value
) implements NbtTagLongArray {

    public long[] valueUnsafe() {
        return value;
    }

    @Override
    public MemorySegment segment() {
        return MemorySegment.ofArray(value).asReadOnly();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NbtTagLongArrayImpl other && Arrays.equals(other.value, value);
    }

    @Override
    public String toString() {
        return "NbtTagLongArrayImpl[" +
                "value=" + Arrays.toString(value) +
                ']';
    }
}
