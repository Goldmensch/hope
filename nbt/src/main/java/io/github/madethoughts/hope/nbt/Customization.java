package io.github.madethoughts.hope.nbt;

import io.github.madethoughts.hope.nbt.serialization.FastDataOutputStream;

import java.io.*;
import java.util.zip.*;

public class Customization {

    public static final Customization DEFAULT = new Customization();

    public InputStream bufferedInputStream(InputStream stream) {
        return new BufferedInputStream(stream);
    }

    public InputStream bufferedInputStream(InputStream stream, int size) {
        return new BufferedInputStream(stream, size);
    }

    public OutputStream bufferedOutputStream(OutputStream stream) {
        return new BufferedOutputStream(stream);
    }

    public OutputStream bufferedOutputStream(OutputStream stream, int size) {
        return new BufferedOutputStream(stream, size);
    }

    public InputStream gzipInputStream(InputStream stream) throws IOException {
        return new GZIPInputStream(stream);
    }

    public OutputStream gzipOutputStream(OutputStream stream) throws IOException {
        return new GZIPOutputStream(stream);
    }

    public InputStream zlibInputStream(InputStream stream) throws IOException {
        return new InflaterInputStream(stream);
    }

    public OutputStream zlibOutputStream(OutputStream stream) throws IOException {
        return new DeflaterOutputStream(stream);
    }

    public DataInput dataInput(InputStream stream) {
        return new DataInputStream(stream);
    }

    public DataOutput dataOutput(OutputStream stream) {
        return new FastDataOutputStream(stream);
    }

}
