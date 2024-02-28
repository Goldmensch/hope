package io.github.madethoughts.hope.nbt.internal.tree;

import io.github.madethoughts.hope.nbt.tree.NbtTagInt;

public record NbtTagIntImpl(
        int value
) implements NbtTagInt {
}
