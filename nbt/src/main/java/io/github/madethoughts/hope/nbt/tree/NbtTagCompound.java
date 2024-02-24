package io.github.madethoughts.hope.nbt.tree;

import java.util.Collections;
import java.util.Map;

public record NbtTagCompound(
        Map<String, NbtTag> values
) implements NbtTag {

    @Override
    public Map<String, NbtTag> values() {
        return Collections.unmodifiableMap(values);
    }
}
