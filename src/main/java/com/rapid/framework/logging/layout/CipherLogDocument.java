package com.rapid.framework.logging.layout;

import com.rapid.framework.logging.constant.Bytecode;
import com.rapid.framework.logging.constant.LogConstants;
import com.rapid.framework.logging.constant.LogLevel;
import com.rapid.framework.logging.io.BytecodeInputStream;
import com.rapid.framework.logging.io.UnsupportedLogVersion;
import com.rapid.framework.logging.security.Cipher;
import com.rapid.framework.logging.security.EmptyCipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CipherLogDocument {
    private final static String LINE_SEPARATOR = System.lineSeparator();
    @SuppressWarnings("SimpleDateFormat")
    private final static DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Cipher cipher = EmptyCipher.INSTANCE;
    private BytecodeInputStream in;
    private short majorVersion;
    private short minorVersion;

    public static CipherLogDocument forToday(String dailyDirPath) throws IOException {
        if (!dailyDirPath.endsWith(File.separator)) {
            dailyDirPath += File.separator;
        }
        String date = FORMAT.format(System.currentTimeMillis());
        File logFile = new File(dailyDirPath + date + LogConstants.SUFFIX_LOG);
        return forFile(logFile);
    }

    public static CipherLogDocument forFile(File logFile) throws IOException {
        if (!logFile.exists()) {
            throw new IOException("Today's log file cannot be open");
        }
        FileInputStream fileInputStream = new FileInputStream(logFile);
        return forStream(fileInputStream);
    }

    public static CipherLogDocument forStream(InputStream logStream) throws IOException {
        BytecodeInputStream inputStream = new BytecodeInputStream(logStream);
        // 读取文件头验证是否为合法日志文件
        byte[] head = new byte[4];
        int res = inputStream.read(head);
        if (res == -1) {
            throw new IOException("invalid log file");
        }
        if (head[0] != (byte) 0xff
                || head[1] != (byte) 0x11
                || head[2] != (byte) 0x00
                || head[3] != (byte) 0x99) {
            throw new IOException("invalid log file");
        }
        return new CipherLogDocument(inputStream);
    }

    public CipherLogDocument(BytecodeInputStream in) throws IOException {
        this.in = in;
        this.majorVersion = in.readU2();
        this.minorVersion = in.readU2();
    }

    public void setCipher(Cipher cipher) {
        if (cipher == null) {
            cipher = EmptyCipher.INSTANCE;
        }
        this.cipher = cipher;
    }

    public String getContent() throws UnsupportedLogVersion {
        return getContent(LogLine.DEFAULT_FORMAT);
    }

    public String getContent(String format) throws UnsupportedLogVersion {
        List<LogLine> logLines = readLogLines();
        StringBuilder builder = new StringBuilder();
        for (LogLine logLine : logLines) {
            String lineString = LogStringer.toString(logLine, format);
            builder.append(lineString);
            builder.append(LINE_SEPARATOR);
        }
        return builder.toString();
    }

    public List<LogLine> readLogLines() throws UnsupportedLogVersion {
        if (majorVersion == Bytecode.MAJOR_VERSION_1) {
            return readLogLinesV1();
        } else {
            throw new UnsupportedLogVersion("Cannot open log file for version " + majorVersion);
        }
    }

    private List<LogLine> readLogLinesV1() {
        List<LogLine> result = new ArrayList<>();
        ConstantPool pool = new ConstantPool();
        while (true) {
            try {
                byte tag = in.readU1();
                if (tag == Bytecode.TAG_POOL) {
                    int poolSize = in.readU4Int();
                    for (int i = 0; i < poolSize; i++) {
                        int byteLength = in.readU4Int();
                        String text = in.readString(byteLength);
                        pool.addConstant(text);
                    }
                } else if (tag == Bytecode.TAG_LOG) {
                    // 读取时间
                    long timestamp = in.readU8Long();
                    // 读取日志等级
                    LogLevel level = LogLevel.fromValue(in.readU1());
                    // 读取tag
                    String nameTag = readAndDecodeString(pool);
                    // 读取message
                    String messsage = readAndDecodeString(pool);
                    LogLine logLine = new LogLine(timestamp, level, nameTag, messsage);
                    // 读取threadName
                    logLine.threadName = readAndDecodeString(pool);
                    // 读取ClassName
                    logLine.className = readAndDecodeString(pool);
                    // 读取MethodName
                    logLine.methodName = readAndDecodeString(pool);
                    // 读取SourceFile
                    logLine.sourceFile = readAndDecodeString(pool);
                    // 读取SourceLine
                    logLine.sourceLine = in.readU4Int();
                    result.add(logLine);
                } else {
                    throw new IOException("invalid tag: " + tag);
                }
            } catch (IOException e) {
                break;
            }
        }
        normalizeLogLines(result);
        return result;
    }

    private String readAndDecodeString(ConstantPool pool) throws IOException {
        int poolIndex = in.readU4Int();
        String text;
        if (poolIndex < 0) {
            text = null;
        } else {
            text = pool.at(poolIndex);
            text = cipher.decode(text);
        }
        return text;
    }

    private void normalizeLogLines(List<LogLine> logLines) {
        // 先分析每个字段最长的长度是多少
        int maxLevelLength = 0;
        int maxTagLength = 0;
        int maxMessageLength = 0;
        int maxThreadNameLength = 0;
        int maxClassNameLength = 0;
        int maxMethodNameLength = 0;
        int maxSourceFileLength = 0;
        int maxSourceLineLength = 0;
        for (LogLine logLine : logLines) {
            maxLevelLength = Math.max(logLine.level.getLetter().length(), maxLevelLength);
            maxTagLength = Math.max(logLine.tag.length(), maxTagLength);
            maxMessageLength = Math.max(logLine.message.length(), maxMessageLength);

            if (logLine.threadName != null) {
                maxThreadNameLength = Math.max(logLine.threadName.length(), maxThreadNameLength);
            }

            if (logLine.className != null) {
                maxClassNameLength = Math.max(logLine.className.length(), maxClassNameLength);
            }

            if (logLine.methodName != null) {
                maxMethodNameLength = Math.max(logLine.methodName.length(), maxMethodNameLength);
            }

            if (logLine.sourceFile != null) {
                maxSourceFileLength = Math.max(logLine.sourceFile.length(), maxSourceFileLength);
            }

            maxSourceLineLength = Math.max(String.valueOf(logLine.sourceLine).length(), maxSourceLineLength);
        }
        // 对每一行log做一下空格间距的标准化
        for (LogLine logLine : logLines) {
            logLine.spaceAfterLevel = maxLevelLength - logLine.level.getLetter().length();
            logLine.spaceAfterTag = maxTagLength - logLine.tag.length();
            logLine.spaceAfterMessage = maxMessageLength - logLine.message.length();

            if (logLine.threadName != null) {
                logLine.spaceAfterThreadName = maxThreadNameLength - logLine.threadName.length();
            }

            if (logLine.className != null) {
                logLine.spaceAfterClassName = maxClassNameLength - logLine.className.length();
            }

            if (logLine.methodName != null) {
                logLine.spaceAfterMethodName = maxMethodNameLength - logLine.methodName.length();
            }

            if (logLine.sourceFile != null) {
                logLine.spaceAfterSourceFile = maxSourceFileLength - logLine.sourceFile.length();
            }

            logLine.spaceAfterSourceLine = maxSourceLineLength - String.valueOf(logLine.sourceLine).length();
        }
    }
}
