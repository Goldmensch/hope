package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtTagByteArrayImpl;

import java.lang.foreign.MemorySegment;

public sealed interface NbtTagByteArray extends NbtTag permits NbtTagByteArrayImpl {
    static NbtTagByteArray byteArrayTag(byte... val) {
        return new NbtTagByteArrayImpl(val);
    }

    byte[] value();

    MemorySegment segment();
}
