package io.github.madethoughts.hope.nbt.tree;

public record RootCompound(
        String name,

        NbtTagCompound compound
) {
}
