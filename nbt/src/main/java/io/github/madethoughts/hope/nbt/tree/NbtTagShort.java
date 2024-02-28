package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtTagShortImpl;

public sealed interface NbtTagShort extends NbtTag permits NbtTagShortImpl {
    static NbtTagShort shortTag(short val) {
        return new NbtTagShortImpl(val);
    }

    short value();
}
