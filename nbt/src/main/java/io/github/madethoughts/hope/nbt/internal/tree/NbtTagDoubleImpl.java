package io.github.madethoughts.hope.nbt.internal.tree;

import io.github.madethoughts.hope.nbt.tree.NbtTagDouble;

public record NbtTagDoubleImpl(
        double value
) implements NbtTagDouble {
}
