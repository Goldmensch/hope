package io.github.madethoughts.hope.nbt.tree;

public sealed interface NbtTag permits NbtTagByte, NbtTagByteArray, NbtTagCompound, NbtTagDouble, NbtTagFloat, NbtTagInt, NbtTagIntArray, NbtTagList, NbtTagLong, NbtTagLongArray, NbtTagShort, NbtTagString {
}
