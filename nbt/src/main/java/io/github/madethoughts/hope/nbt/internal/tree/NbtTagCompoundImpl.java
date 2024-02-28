package io.github.madethoughts.hope.nbt.internal.tree;

import io.github.madethoughts.hope.nbt.tree.NbtTag;
import io.github.madethoughts.hope.nbt.tree.NbtTagCompound;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record NbtTagCompoundImpl(
        Map<String, NbtTag> value
) implements NbtTagCompound {

    @Override
    public Map<String, NbtTag> value() {
        return Collections.unmodifiableMap(value);
    }

    public static final class NbtTagCompoundBuilder implements io.github.madethoughts.hope.nbt.tree.NbtTagCompound.NbtTagCompoundBuilder {

        private final Map<String, NbtTag> tags = new HashMap<>();

        public NbtTagCompoundBuilder() {
        }

        public NbtTagCompoundBuilder put(String name, NbtTag val) {
            tags.put(name, val);
            return this;
        }

        public NbtTagCompoundImpl build() {
            return new NbtTagCompoundImpl(tags);
        }
    }
}
