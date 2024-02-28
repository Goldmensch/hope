package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtTagDoubleImpl;

public sealed interface NbtTagDouble extends NbtTag permits NbtTagDoubleImpl {
    static NbtTagDouble doubleTag(double val) {
        return new NbtTagDoubleImpl(val);
    }

    double value();
}
