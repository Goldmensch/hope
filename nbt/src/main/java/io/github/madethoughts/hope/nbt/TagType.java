package io.github.madethoughts.hope.nbt;

import io.github.madethoughts.hope.nbt.tree.*;

public enum TagType {
    END(0),
    BYTE(1),
    SHORT(2),
    INT(3),
    LONG(4),
    FLOAT(5),
    DOUBLE(6),
    BYTE_ARRAY(7),
    STRING(8),
    LIST(9),
    COMPOUND(10),
    INT_ARRAY(11),
    LONG_ARRAY(12);

    private final int id;

    TagType(int id) {
        this.id = id;
    }

    public static TagType byId(int id) {
        return switch (id) {
            case 0 -> END;
            case 1 -> BYTE;
            case 2 -> SHORT;
            case 3 -> INT;
            case 4 -> LONG;
            case 5 -> FLOAT;
            case 6 -> DOUBLE;
            case 7 -> BYTE_ARRAY;
            case 8 -> STRING;
            case 9 -> LIST;
            case 10 -> COMPOUND;
            case 11 -> INT_ARRAY;
            case 12 -> LONG_ARRAY;
            default -> throw new IllegalArgumentException("Unknown  id %s".formatted(id));
        };
    }

    public static TagType byTagClass(NbtTag tag) {
        return switch (tag) {
            case NbtTagByte _ -> BYTE;
            case NbtTagShort _ -> SHORT;
            case NbtTagInt _ -> INT;
            case NbtTagLong _ -> LONG;
            case NbtTagFloat _ -> FLOAT;
            case NbtTagDouble _ -> DOUBLE;
            case NbtTagByteArray _ -> BYTE_ARRAY;
            case NbtTagString _ -> STRING;
            case NbtTagList<?> _ -> LIST;
            case NbtTagCompound _ -> COMPOUND;
            case NbtTagIntArray _ -> INT_ARRAY;
            case NbtTagLongArray _ -> LONG_ARRAY;
        };
    }

    public int id() {
        return id;
    }
}
