package io.github.madethoughts.hope.nbt.tree;

public record NbtTagByteArray(
        byte[] value
) implements NbtTag {
}
