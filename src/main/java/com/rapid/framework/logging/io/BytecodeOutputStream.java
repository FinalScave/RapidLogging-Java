package com.rapid.framework.logging.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class BytecodeOutputStream extends FilterOutputStream {
    public BytecodeOutputStream(OutputStream out) {
        super(out);
    }

    public void writeU1(byte b) throws IOException {
        write(new byte[]{b});
    }

    public void writeU1(int n) throws IOException {
        byte[] bytes = ByteBuffer.allocate(1).putInt(n).array();
        write(bytes);
    }

    public void writeU2(short n) throws IOException {
        byte[] bytes = ByteBuffer.allocate(2).putShort(n).array();
        write(bytes);
    }

    public void writeU2(int n) throws IOException {
        byte[] bytes = ByteBuffer.allocate(2).putInt(n).array();
        write(bytes);
    }

    public void writeU4(int n) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).putInt(n).array();
        write(bytes);
    }

    public void writeU4(float n) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).putFloat(n).array();
        write(bytes);
    }

    public void writeU8(long n) throws IOException {
        byte[] bytes = ByteBuffer.allocate(8).putLong(n).array();
        write(bytes);
    }

    public void writeU8(double n) throws IOException {
        byte[] bytes = ByteBuffer.allocate(8).putDouble(n).array();
        write(bytes);
    }
}
