package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtTagLongArrayImpl;

import java.lang.foreign.MemorySegment;

public sealed interface NbtTagLongArray extends NbtTag permits NbtTagLongArrayImpl {
    static NbtTagLongArray longArray(long... val) {
        return new NbtTagLongArrayImpl(val);
    }

    long[] value();

    MemorySegment segment();
}
