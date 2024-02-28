package io.github.madethoughts.hope.nbt;

import io.github.madethoughts.hope.nbt.internal.tree.*;
import io.github.madethoughts.hope.nbt.tree.NbtTag;

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
            case NbtTagByteImpl _ -> BYTE;
            case NbtTagShortImpl _ -> SHORT;
            case NbtTagIntImpl _ -> INT;
            case NbtTagLongImpl _ -> LONG;
            case NbtTagFloatImpl _ -> FLOAT;
            case NbtTagDoubleImpl _ -> DOUBLE;
            case NbtTagByteArrayImpl _ -> BYTE_ARRAY;
            case NbtTagStringImpl _ -> STRING;
            case NbtTagListImpl<?> _ -> LIST;
            case NbtTagCompoundImpl _ -> COMPOUND;
            case NbtTagIntArrayImpl _ -> INT_ARRAY;
            case NbtTagLongArrayImpl _ -> LONG_ARRAY;
        };
    }

    public int id() {
        return id;
    }
}
