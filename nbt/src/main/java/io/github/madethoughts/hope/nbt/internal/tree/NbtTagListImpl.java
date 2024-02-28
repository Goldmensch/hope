package io.github.madethoughts.hope.nbt.internal.tree;

import io.github.madethoughts.hope.nbt.tree.NbtTag;
import io.github.madethoughts.hope.nbt.tree.NbtTagList;

import java.util.Collections;
import java.util.List;

public record NbtTagListImpl<T extends NbtTag>(
        List<T> values
) implements NbtTagList<T> {
    @Override
    public List<T> value() {
        return Collections.unmodifiableList(values);
    }
}
