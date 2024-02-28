package io.github.madethoughts.hope.nbt.internal.tree;

import io.github.madethoughts.hope.nbt.tree.NbtTagLong;

public record NbtTagLongImpl(
        long value
) implements NbtTagLong {
}
