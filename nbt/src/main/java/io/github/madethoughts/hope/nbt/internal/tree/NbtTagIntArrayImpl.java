package io.github.madethoughts.hope.nbt.internal.tree;

import io.github.madethoughts.hope.nbt.tree.NbtTagIntArray;

import java.lang.foreign.MemorySegment;
import java.util.Arrays;

public record NbtTagIntArrayImpl(
        int[] value
) implements NbtTagIntArray {

    public int[] valueUnsafe() {
        return value;
    }

    @Override
    public int[] value() {
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public MemorySegment segment() {
        return MemorySegment.ofArray(value).asReadOnly();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NbtTagIntArrayImpl other && Arrays.equals(other.value, value);
    }

    @Override
    public String toString() {
        return "NbtTagIntArrayImpl[" +
                "value=" + Arrays.toString(value) +
                ']';
    }
}
