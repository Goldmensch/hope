package io.github.madethoughts.hope.nbt.internal.tree;

import io.github.madethoughts.hope.nbt.tree.NbtTagFloat;

public record NbtTagFloatImpl(
        float value
) implements NbtTagFloat {
}
