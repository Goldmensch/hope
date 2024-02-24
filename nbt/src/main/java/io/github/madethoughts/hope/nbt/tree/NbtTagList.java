package io.github.madethoughts.hope.nbt.tree;

import java.util.Collections;
import java.util.List;

public record NbtTagList<T extends NbtTag>(
        List<T> values
) implements NbtTag {
    @Override
    public List<T> values() {
        return Collections.unmodifiableList(values);
    }
}
