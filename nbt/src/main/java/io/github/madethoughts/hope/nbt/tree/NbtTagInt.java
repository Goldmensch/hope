package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtTagIntImpl;

public sealed interface NbtTagInt extends NbtTag permits NbtTagIntImpl {
    static NbtTagInt intTag(int val) {
        return new NbtTagIntImpl(val);
    }

    int value();
}
