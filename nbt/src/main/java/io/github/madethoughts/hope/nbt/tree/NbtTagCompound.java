package io.github.madethoughts.hope.nbt.tree;

import java.util.Map;

public record NbtTagCompound(
        Map<String, NbtTag> values
) implements NbtTag {
}
