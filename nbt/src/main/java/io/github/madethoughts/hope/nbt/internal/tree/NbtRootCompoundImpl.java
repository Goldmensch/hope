package io.github.madethoughts.hope.nbt.internal.tree;

import io.github.madethoughts.hope.nbt.tree.NbtRootCompound;
import io.github.madethoughts.hope.nbt.tree.NbtTagCompound;

public record NbtRootCompoundImpl(
        String name,

        NbtTagCompound compound
) implements NbtRootCompound {
}
