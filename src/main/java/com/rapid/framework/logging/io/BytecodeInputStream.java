package com.rapid.framework.logging.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BytecodeInputStream extends FilterInputStream {
    public BytecodeInputStream(InputStream in) {
        super(in);
    }

    public byte readU1() throws IOException {
        byte[] bytes = ByteBuffer.allocate(1).array();
        if(read(bytes) != -1) {
            return bytes[0];
        } else {
            throw new IOException("read u1 error");
        }
    }

    public short readU2() throws IOException {
        byte[] bytes = ByteBuffer.allocate(2).array();
        if(read(bytes) != -1) {
            return ByteBuffer.wrap(bytes).getShort();
        } else {
            throw new IOException("read u2 error");
        }
    }

    public int readU4Int() throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).array();
        if(read(bytes) != -1) {
            return ByteBuffer.wrap(bytes).getInt();
        } else {
            throw new IOException("read u4 int error");
        }
    }

    public float readU4Float() throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).array();
        if(read(bytes) != -1) {
            return ByteBuffer.wrap(bytes).getFloat();
        } else {
            throw new IOException("read u4 float error");
        }
    }

    public long readU8Long() throws IOException {
        byte[] bytes = ByteBuffer.allocate(8).array();
        if(read(bytes) != -1) {
            return ByteBuffer.wrap(bytes).getLong();
        } else {
            throw new IOException("read u8 long error");
        }
    }

    public double readU8Double() throws IOException {
        byte[] bytes = ByteBuffer.allocate(8).array();
        if(read(bytes) != -1) {
            return ByteBuffer.wrap(bytes).getDouble();
        } else {
            throw new IOException("read u8 double error");
        }
    }

    public String readString(int byteLength) throws IOException {
        byte[] bytes = ByteBuffer.allocate(byteLength).array();
        if(read(bytes) != -1) {
            return new String(bytes);
        } else {
            throw new IOException("read string error");
        }
    }

    public String readUTF8(int byteLength) throws IOException {
        byte[] bytes = ByteBuffer.allocate(byteLength).array();
        if(read(bytes) != -1) {
            return new String(bytes);
        } else {
            throw new IOException("read utf8 error");
        }
    }
}
