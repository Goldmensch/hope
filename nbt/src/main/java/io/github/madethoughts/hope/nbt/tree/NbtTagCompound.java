package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtTagCompoundImpl;

import java.util.Map;

public sealed interface NbtTagCompound extends NbtTag permits NbtTagCompoundImpl {

    static NbtTagCompound ofMap(Map<String, NbtTag> map) {
        return new NbtTagCompoundImpl(map);
    }

    static NbtTagCompoundBuilder builder() {
        return new NbtTagCompoundImpl.NbtTagCompoundBuilder();
    }

    Map<String, NbtTag> value();

    interface NbtTagCompoundBuilder {
        NbtTagCompoundBuilder put(String name, NbtTag tag);

        NbtTagCompound build();
    }
}
