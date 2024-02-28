import io.github.madethoughts.hope.nbt.Compression;
import io.github.madethoughts.hope.nbt.Mode;
import io.github.madethoughts.hope.nbt.deserialization.NbtDeserializer;
import io.github.madethoughts.hope.nbt.tree.NbtRootCompound;
import io.github.madethoughts.hope.nbt.tree.NbtTagCompound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

public class DeserializerTest {

    @Test
    public void testUncompressed() {
        NbtRootCompound deserialized = NbtDeserializer.deserialize(new ByteArrayInputStream(Constants.BINARY), Mode.FILE);
        Assertions.assertEquals(Constants.HOPE_NBT, deserialized);
    }

    @Test
    public void testGzip() {
        NbtRootCompound deserialized = NbtDeserializer.deserialize(new ByteArrayInputStream(Constants.BINARY_GZIP), Mode.FILE, Compression.GZIP);
        Assertions.assertEquals(Constants.HOPE_NBT, deserialized);
    }

    @Test
    public void testZlib() {
        NbtRootCompound deserialized = NbtDeserializer.deserialize(new ByteArrayInputStream(Constants.BINARY_ZLIB), Mode.FILE, Compression.ZLIB);
        Assertions.assertEquals(Constants.HOPE_NBT, deserialized);
    }

    @Test
    public void testUncompressedNetworking() {
        NbtTagCompound compound = Constants.HOPE_NBT.compound();

        NbtRootCompound deserialized = NbtDeserializer.deserialize(new ByteArrayInputStream(Constants.BINARY_NETWORK), Mode.NETWORK);
        Assertions.assertEquals(NbtRootCompound.rootCompoundTag("", compound), deserialized);
    }
}
