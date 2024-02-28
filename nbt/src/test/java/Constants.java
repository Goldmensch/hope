import io.github.madethoughts.hope.nbt.internal.tree.NbtRootCompoundImpl;
import io.github.madethoughts.hope.nbt.tree.*;
import net.kyori.adventure.nbt.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Constants {
    public static final byte[] first1000Val;

    static {
        first1000Val = new byte[1000];
        for (int i = 0; i < 1000; i++) {
            first1000Val[i] = (byte) ((i * i * 255 + i * 7) % 100);
        }
    }
    public static final CompoundBinaryTag ADV_NBT = CompoundBinaryTag.builder()
            .put("byteTest", ByteBinaryTag.byteBinaryTag((byte) 127))
            .put("shortTest", ShortBinaryTag.shortBinaryTag(((short) 32767)))
            .put("intTestPos", IntBinaryTag.intBinaryTag(2147483647))
            .put("intTestNeg", IntBinaryTag.intBinaryTag(-2147483647))
            .put("longTest", LongBinaryTag.longBinaryTag(9223372036854775807L))
            .put("floatTest", FloatBinaryTag.floatBinaryTag(0.49823147058486938F))
            .put("doubleTest", DoubleBinaryTag.doubleBinaryTag(0.49312871321823148D))
            .put("byteArrayTest", ByteArrayBinaryTag.byteArrayBinaryTag(first1000Val))
            .put("stringTest", StringBinaryTag.stringBinaryTag("U asshole"))
            .put("listTest (compound)", ListBinaryTag.listBinaryTag(BinaryTagTypes.COMPOUND, List.of(
                    CompoundBinaryTag.from(Map.of(
                            "created-on", LongBinaryTag.longBinaryTag(1264099775885L),
                            "name", StringBinaryTag.stringBinaryTag("Compound tag #0")
                    )),
                    CompoundBinaryTag.from(Map.of(
                            "created-on", LongBinaryTag.longBinaryTag(1264099775885L),
                            "name", StringBinaryTag.stringBinaryTag("Compound tag #1")
                    ))
            )))
            .put("listTest (long)", ListBinaryTag.listBinaryTag(BinaryTagTypes.LONG, List.of(
                    LongBinaryTag.longBinaryTag(11),
                    LongBinaryTag.longBinaryTag(12),
                    LongBinaryTag.longBinaryTag(13),
                    LongBinaryTag.longBinaryTag(14),
                    LongBinaryTag.longBinaryTag(15)
            )))
            .put("nested compound test", CompoundBinaryTag.builder()
                    .put("egg", CompoundBinaryTag.builder()
                            .put("name", StringBinaryTag.stringBinaryTag("Eggbert"))
                            .put("value", FloatBinaryTag.floatBinaryTag(0.5F))
                            .build()
                    )
                    .put("ham", CompoundBinaryTag.builder()
                            .put("name", StringBinaryTag.stringBinaryTag("Hampus"))
                            .put("value", FloatBinaryTag.floatBinaryTag(0.75F))
                            .build()
                    )
                    .build()
            )
            .put("intArrayTest", IntArrayBinaryTag.intArrayBinaryTag(Integer.MAX_VALUE, 1, 2, 3, 4, 5, 6, 77, 888, 9999, 0))
            .put("longArrayTest", LongArrayBinaryTag.longArrayBinaryTag(Long.MAX_VALUE, 0, 9, 88, 777, 6666, 55555, 444444, 3333333, 22222222, 1111111111))
            .build();
    public static final Map.Entry<String, CompoundBinaryTag> ADV_NBT_NAMED = Map.entry("level", ADV_NBT);
    public static final NbtRootCompoundImpl HOPE_NBT = new NbtRootCompoundImpl("level", NbtTagCompound.builder()
            .put("byteTest", NbtTagByte.byteTag((byte) 127))
            .put("shortTest", NbtTagShort.shortTag(((short) 32767)))
            .put("intTestPos", NbtTagInt.intTag(2147483647))
            .put("intTestNeg", NbtTagInt.intTag(-2147483647))
            .put("longTest", NbtTagLong.longTag(9223372036854775807L))
            .put("floatTest", NbtTagFloat.floatTag(0.49823147058486938F))
            .put("doubleTest", NbtTagDouble.doubleTag(0.49312871321823148D))
            .put("byteArrayTest", NbtTagByteArray.byteArrayTag(first1000Val))
            .put("stringTest", NbtTagString.stringTag("U asshole"))
            .put("listTest (compound)", NbtTagList.ofList(List.of(
                    NbtTagCompound.ofMap(Map.of(
                            "created-on", NbtTagLong.longTag(1264099775885L),
                            "name", NbtTagString.stringTag("Compound tag #0")
                    )),
                    NbtTagCompound.ofMap(Map.of(
                            "created-on", NbtTagLong.longTag(1264099775885L),
                            "name", NbtTagString.stringTag("Compound tag #1")
                    ))
            )))
            .put("listTest (long)", NbtTagList.ofList(List.of(
                    NbtTagLong.longTag(11),
                    NbtTagLong.longTag(12),
                    NbtTagLong.longTag(13),
                    NbtTagLong.longTag(14),
                    NbtTagLong.longTag(15)
            )))
            .put("nested compound test", NbtTagCompound.builder()
                    .put("egg", NbtTagCompound.builder()
                            .put("name", NbtTagString.stringTag("Eggbert"))
                            .put("value", NbtTagFloat.floatTag(0.5F))
                            .build()
                    )
                    .put("ham", NbtTagCompound.builder()
                            .put("name", NbtTagString.stringTag("Hampus"))
                            .put("value", NbtTagFloat.floatTag(0.75F))
                            .build()
                    )
                    .build()
            )
            .put("intArrayTest", NbtTagIntArray.intArrayTag(Integer.MAX_VALUE, 1, 2, 3, 4, 5, 6, 77, 888, 9999, 0))
            .put("longArrayTest", NbtTagLongArray.longArray(Long.MAX_VALUE, 0, 9, 88, 777, 6666, 55555, 444444, 3333333, 22222222, 1111111111))
            .build()
    );
    public static final byte[] BINARY;
    public static final byte[] BINARY_GZIP;
    public static final byte[] BINARY_ZLIB;
    public static final byte[] BINARY_NETWORK;

    static {
        try {
            var outputStream = new ByteArrayOutputStream();
            BinaryTagIO.writer().writeNamed(ADV_NBT_NAMED, outputStream);
            BINARY = outputStream.toByteArray();

            var outputStreamGzip = new ByteArrayOutputStream();
            BinaryTagIO.writer().writeNamed(ADV_NBT_NAMED, outputStreamGzip, BinaryTagIO.Compression.GZIP);
            BINARY_GZIP = outputStreamGzip.toByteArray();

            var outputStreamZlib = new ByteArrayOutputStream();
            BinaryTagIO.writer().writeNamed(ADV_NBT_NAMED, outputStreamZlib, BinaryTagIO.Compression.ZLIB);
            BINARY_ZLIB = outputStreamZlib.toByteArray();

            var outputStreamNetwork = new ByteArrayOutputStream();
            BinaryTagIO.writer().writeNameless(ADV_NBT, outputStreamNetwork);
            BINARY_NETWORK = outputStreamNetwork.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
