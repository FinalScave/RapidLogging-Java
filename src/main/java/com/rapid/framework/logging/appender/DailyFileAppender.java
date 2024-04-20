package com.rapid.framework.logging.appender;

import com.rapid.framework.logging.Logger;
import com.rapid.framework.logging.constant.Bytecode;
import com.rapid.framework.logging.constant.LogConstants;
import com.rapid.framework.logging.io.BytecodeFileInputStream;
import com.rapid.framework.logging.io.BytecodeFileOutputStream;
import com.rapid.framework.logging.layout.ConstantPool;
import com.rapid.framework.logging.layout.LogLine;
import com.rapid.framework.logging.lock.FileMultiProcessSync;
import com.rapid.framework.logging.security.Cipher;
import com.rapid.framework.logging.security.EmptyCipher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DailyFileAppender implements CipherAppender {
    @SuppressWarnings("SimpleDateFormat")
    private final static DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final File file;
    private final boolean isFirstTime;
    private BytecodeFileOutputStream out;
    private FileMultiProcessSync multiProcessSync;
    private Cipher cipher = EmptyCipher.INSTANCE;
    private ConstantPool existsPool;

    public static DailyFileAppender into(String dirPath) throws IOException {
        return into(dirPath, true);
    }

    /**
     * 创建一个日报输出器
     *
     * @param dirPath       输出目录
     * @param keepYesterday 是否保留往日的日报，建议不保留
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static DailyFileAppender into(String dirPath, boolean keepYesterday) throws IOException {
        if (!dirPath.endsWith(File.separator)) {
            dirPath += File.separator;
        }
        long today = System.currentTimeMillis();
        if (!keepYesterday) {
            // 如果不保留以往的日报，那么就检测一下把昨天的日报删除了
            long yesterday = today - 24 * 60 * 60 * 1000L;
            String yesterdayDate = FORMAT.format(yesterday);
            File yesterdayFile = new File(dirPath + yesterdayDate + LogConstants.SUFFIX_LOG);
            if (yesterdayFile.exists()) {
                yesterdayFile.delete();
            }
        }
        // 创建今天的日报文件
        String date = FORMAT.format(today);
        File logFile = new File(dirPath + date + LogConstants.SUFFIX_LOG);
        boolean exists = logFile.exists();
        if (!exists) {
            logFile.createNewFile();
        }
        return new DailyFileAppender(logFile, !exists);
    }

    private DailyFileAppender(File file, boolean isFirstTime) {
        this.file = file;
        this.isFirstTime = isFirstTime;
    }

    @Override
    public void setCipher(Cipher cipher) {
        if (cipher == null) {
            cipher = EmptyCipher.INSTANCE;
        }
        this.cipher = cipher;
    }

    @Override
    public void append(LogLine logLine) {
        try {
            checkOutputAndConstantPool();

            tryLockProcess();

            writeLogBytecode(logLine);
        } catch (IOException ignore) {
        } finally {
            tryUnlockProcess();
        }
    }

    private void writeLogBytecode(LogLine logLine) throws IOException {
        // 判断是否需要写入新的常量池，如果需要，则写入常量池
        checkShouldWriteNewConstantPoolThenWrite(logLine);
        // 写入日志块的标志位
        out.writeU1(Bytecode.TAG_LOG);
        // 写入时间
        out.writeU8(logLine.timestamp);
        // 写入日志等级
        out.writeU1(logLine.level.getValue());
        // 写入tag
        out.writeU4(existsPool.getConstant(logLine.tag));
        // 写入message
        out.writeU4(existsPool.getConstant(logLine.message));
        // 写入threadName
        out.writeU4(existsPool.getConstant(logLine.threadName));
        // 写入className
        out.writeU4(existsPool.getConstant(logLine.className));
        // 写入methodName
        out.writeU4(existsPool.getConstant(logLine.methodName));
        // 写入sourceFile
        out.writeU4(existsPool.getConstant(logLine.sourceFile));
        // 写入sourceLine
        out.writeU4(logLine.sourceLine);
    }

    private void checkShouldWriteNewConstantPoolThenWrite(LogLine logLine) throws IOException {
        ConstantPool newPool = new ConstantPool();
        logLine.tag = encodeAndAddToPool(logLine.tag, newPool);
        logLine.message = encodeAndAddToPool(logLine.message, newPool);
        logLine.threadName = encodeAndAddToPool(logLine.threadName, newPool);
        logLine.className = encodeAndAddToPool(logLine.className, newPool);
        logLine.methodName = encodeAndAddToPool(logLine.methodName, newPool);
        logLine.sourceFile = encodeAndAddToPool(logLine.sourceFile, newPool);
        // 如果有新增的常量，则写入新的常量池
        if (!newPool.isEmpty()) {
            out.writeU1(Bytecode.TAG_POOL);
            out.writeU4(newPool.size());
            for (String text : newPool.keys()) {
                byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
                out.writeU4(bytes.length);
                out.write(bytes);
            }
            // 将写入的常量池合并到内存中记录的常量池
            existsPool.merge(newPool);
        }
    }

    private String encodeAndAddToPool(String text, ConstantPool pool) {
        if (text == null) {
            return null;
        }
        String encodedText = cipher.encode(text);
        if (!existsPool.hasConstant(encodedText) && !pool.hasConstant(encodedText)) {
            pool.addConstant(encodedText);
        }
        return encodedText;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkOutputAndConstantPool() throws IOException {
        // 如果不是首次创建，先把之前的所有常量读出来
        if (existsPool == null) {
            existsPool = new ConstantPool();
            if (!isFirstTime) {
                try {
                    BytecodeFileInputStream in = new BytecodeFileInputStream(file);
                    in.skip(Bytecode.LENGTH_HEAD_V1);
                    while (true) {
                        try {
                            byte tag = in.readU1();
                            if (tag == Bytecode.TAG_POOL) {
                                int poolSize = in.readU4Int();
                                for (int i = 0; i < poolSize; i++) {
                                    int byteLength = in.readU4Int();
                                    String str = in.readString(byteLength);
                                    existsPool.addConstant(str);
                                }
                            } else if (tag == Bytecode.TAG_LOG) {
                                in.skip(Bytecode.LENGTH_LOG_V1);
                            } else {
                                throw new IOException("invalid tag: " + tag);
                            }
                        } catch (IOException ignore) {
                            break;
                        }
                    }
                } catch (FileNotFoundException ignore) {
                }
            }
        }
        if (out == null) {
            out = new BytecodeFileOutputStream(file, true);
            // 如果是首次创建文件，则写入文件头
            if (isFirstTime) {
                out.writeU4(Bytecode.MAGIC);
                out.writeU2(Bytecode.MAJOR_VERSION_1);
                out.writeU2(Bytecode.MINOR_VERSION_0);
            }
        }
    }

    private void tryLockProcess() throws IOException {
        if (multiProcessSync == null) {
            if (Logger.getConfig().enableMultiProcessSync) {
                multiProcessSync = new FileMultiProcessSync(out.getFileOut());
            }
        }
        if (multiProcessSync != null) {
            multiProcessSync.lock();
        }
    }

    private void tryUnlockProcess() {
        if (multiProcessSync != null) {
            try {
                multiProcessSync.unlock();
            } catch (IOException ignore) {
            }
        }
    }
}
