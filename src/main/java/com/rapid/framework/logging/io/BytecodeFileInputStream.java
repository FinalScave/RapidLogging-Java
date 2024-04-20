package com.rapid.framework.logging.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BytecodeFileInputStream extends BytecodeInputStream {
    public BytecodeFileInputStream(String path) throws FileNotFoundException {
        super(new FileInputStream(path));
    }

    public BytecodeFileInputStream(File file) throws FileNotFoundException {
        super(new FileInputStream(file));
    }
}
