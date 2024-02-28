package io.github.madethoughts.hope.nbt.tree;

import io.github.madethoughts.hope.nbt.internal.tree.NbtTagFloatImpl;

public sealed interface NbtTagFloat extends NbtTag permits NbtTagFloatImpl {
    static NbtTagFloat floatTag(float val) {
        return new NbtTagFloatImpl(val);
    }

    float value();
}
