import io.github.madethoughts.hope.nbt.Compression;
import io.github.madethoughts.hope.nbt.CompressionType;
import io.github.madethoughts.hope.nbt.Mode;
import io.github.madethoughts.hope.nbt.serialization.NbtSerializer;
import net.kyori.adventure.nbt.BinaryTagIO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SerializerTest {


    @Test
    public void testUncompressed() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        NbtSerializer.serialize(outputStream, Constants.HOPE_NBT, Mode.FILE);

        var read = BinaryTagIO.reader().readNamed(new ByteArrayInputStream(outputStream.toByteArray()));
        Assertions.assertEquals(Constants.ADV_NBT_NAMED, read);
    }

    @Test
    public void testGzip() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        NbtSerializer.serialize(outputStream, Constants.HOPE_NBT, Mode.FILE, new Compression(CompressionType.GZIP));

        var read = BinaryTagIO.reader().readNamed(new ByteArrayInputStream(outputStream.toByteArray()), BinaryTagIO.Compression.GZIP);
        Assertions.assertEquals(Constants.ADV_NBT_NAMED, read);
    }

    @Test
    public void testZlib() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        NbtSerializer.serialize(outputStream, Constants.HOPE_NBT, Mode.FILE, new Compression(CompressionType.ZLIB));

        var read = BinaryTagIO.reader().readNamed(new ByteArrayInputStream(outputStream.toByteArray()), BinaryTagIO.Compression.ZLIB);
        Assertions.assertEquals(Constants.ADV_NBT_NAMED, read);
    }

    @Test
    public void testUncompressedNetworking() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        NbtSerializer.serialize(outputStream, Constants.HOPE_NBT, Mode.NETWORK);

        var read = BinaryTagIO.reader().readNameless(new ByteArrayInputStream(outputStream.toByteArray()));
        Assertions.assertEquals(Constants.ADV_NBT, read);
    }
}
