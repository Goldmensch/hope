package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtTagLongImpl;

public sealed interface NbtTagLong extends NbtTag permits NbtTagLongImpl {
    static NbtTagLong longTag(long val) {
        return new NbtTagLongImpl(val);
    }

    long value();
}
