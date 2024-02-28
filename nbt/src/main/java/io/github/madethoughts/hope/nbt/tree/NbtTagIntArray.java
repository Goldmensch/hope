package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtTagIntArrayImpl;

import java.lang.foreign.MemorySegment;

public sealed interface NbtTagIntArray extends NbtTag permits NbtTagIntArrayImpl {
    static NbtTagIntArray intArrayTag(int... val) {
        return new NbtTagIntArrayImpl(val);
    }

    int[] value();

    MemorySegment segment();
}
