package io.github.madethoughts.hope.nbt.internal.tree;

import io.github.madethoughts.hope.nbt.tree.NbtTagString;

public record NbtTagStringImpl(
        String value
) implements NbtTagString {
}
