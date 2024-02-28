package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtRootCompoundImpl;

public sealed interface NbtRootCompound permits NbtRootCompoundImpl {
    static NbtRootCompound rootCompoundTag(String name, NbtTagCompound compound) {
        return new NbtRootCompoundImpl(name, compound);
    }

    String name();

    NbtTagCompound compound();
}
