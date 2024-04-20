package com.rapid.framework.logging.lock;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * 日志多进程同步工具类
 */
public final class FileMultiProcessSync {
    private FileChannel fileChannel;
    private FileLock fileLock;

    public FileMultiProcessSync(FileOutputStream stream) {
        fileChannel = stream.getChannel();
    }

    public void lock() throws IOException {
        fileLock = fileChannel.lock();
    }

    public void unlock() throws IOException {
        if (fileLock == null) {
            return;
        }
        fileLock.release();
    }
}
