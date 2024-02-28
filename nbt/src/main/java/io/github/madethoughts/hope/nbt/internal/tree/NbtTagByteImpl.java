package io.github.madethoughts.hope.nbt.internal.tree;

import io.github.madethoughts.hope.nbt.tree.NbtTagByte;

public record NbtTagByteImpl(
        byte value
) implements NbtTagByte {
}
