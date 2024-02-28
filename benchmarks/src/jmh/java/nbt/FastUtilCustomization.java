package nbt;

import io.github.madethoughts.hope.nbt.Customization;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;

import java.io.InputStream;
import java.io.OutputStream;

public class FastUtilCustomization extends Customization {

    public static final FastUtilCustomization INSTANCE = new FastUtilCustomization();

    @Override
    public InputStream bufferedInputStream(InputStream stream) {
        return new FastBufferedInputStream(stream);
    }

    @Override
    public InputStream bufferedInputStream(InputStream stream, int size) {
       return new FastBufferedInputStream(stream, size);
    }

    @Override
    public OutputStream bufferedOutputStream(OutputStream stream) {
        return new FastBufferedOutputStream(stream);
    }

    @Override
    public OutputStream bufferedOutputStream(OutputStream stream, int size) {
        return new FastBufferedOutputStream(stream, size);
    }
}
