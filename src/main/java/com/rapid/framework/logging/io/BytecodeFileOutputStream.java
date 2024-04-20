package com.rapid.framework.logging.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class BytecodeFileOutputStream extends BytecodeOutputStream {
    public BytecodeFileOutputStream(String path) throws FileNotFoundException {
        super(new FileOutputStream(path));
    }

    public BytecodeFileOutputStream(File file) throws FileNotFoundException {
        super(new FileOutputStream(file));
    }

    public BytecodeFileOutputStream(String path, boolean append) throws FileNotFoundException {
        super(new FileOutputStream(path, append));
    }

    public BytecodeFileOutputStream(File file, boolean append) throws FileNotFoundException {
        super(new FileOutputStream(file, append));
    }

    public FileOutputStream getFileOut() {
        return (FileOutputStream) out;
    }
}
