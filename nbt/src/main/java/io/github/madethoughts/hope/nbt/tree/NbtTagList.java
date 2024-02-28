package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtTagListImpl;

import java.util.List;

public sealed interface NbtTagList<T extends NbtTag> extends NbtTag permits NbtTagListImpl {
    static <T extends NbtTag> NbtTagList<T> ofList(List<T> val) {
        return new NbtTagListImpl<>(val);
    }

    List<T> value();
}
