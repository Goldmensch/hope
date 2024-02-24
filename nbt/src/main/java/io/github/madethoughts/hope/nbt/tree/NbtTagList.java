package io.github.madethoughts.hope.nbt.tree;

import java.util.List;

public record NbtTagList<T extends NbtTag>(
        List<T> values
) implements NbtTag {
}
