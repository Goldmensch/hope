package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtTagStringImpl;

public sealed interface NbtTagString extends NbtTag permits NbtTagStringImpl {
    static NbtTagString stringTag(String val) {
        return new NbtTagStringImpl(val);
    }

    String value();
}
