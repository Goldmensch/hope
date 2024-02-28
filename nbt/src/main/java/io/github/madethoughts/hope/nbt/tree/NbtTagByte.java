package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtTagByteImpl;

public sealed interface NbtTagByte extends NbtTag permits NbtTagByteImpl {
    static NbtTagByte byteTag(byte val) {
        return new NbtTagByteImpl(val);
    }

    byte value();
}
