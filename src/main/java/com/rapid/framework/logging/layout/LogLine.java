package com.rapid.framework.logging.layout;

import com.rapid.framework.logging.constant.LogLevel;

public class LogLine {
    public final static String DEFAULT_FORMAT = "%d  %t  [%l]  %i  %s  %c  %m  %o  %n";

    public long timestamp;
    public LogLevel level;
    public String tag;
    public String message;
    public String threadName;
    public String className;
    public String methodName;
    public String sourceFile;
    public int sourceLine;
    protected int spaceAfterTime;
    protected int spaceAfterLevel;
    protected int spaceAfterTag;
    protected int spaceAfterMessage;
    protected int spaceAfterThreadName;
    protected int spaceAfterClassName;
    protected int spaceAfterMethodName;
    protected int spaceAfterSourceFile;
    protected int spaceAfterSourceLine;

    public LogLine(long timestamp, LogLevel level, String tag, String message) {
        this.timestamp = timestamp;
        this.level = level;
        this.tag = tag;
        this.message = message;
    }

    public int getSpaceAfterTime() {
        return spaceAfterTime;
    }

    public int getSpaceAfterThreadName() {
        return spaceAfterThreadName;
    }

    public int getSpaceAfterLevel() {
        return spaceAfterLevel;
    }

    public int getSpaceAfterTag() {
        return spaceAfterTag;
    }

    public int getSpaceAfterMessage() {
        return spaceAfterMessage;
    }

    public int getSpaceAfterClassName() {
        return spaceAfterClassName;
    }

    public int getSpaceAfterMethodName() {
        return spaceAfterMethodName;
    }

    public int getSpaceAfterSourceFile() {
        return spaceAfterSourceFile;
    }

    public int getSpaceAfterSourceLine() {
        return spaceAfterSourceLine;
    }

    @Override
    public String toString() {
        return LogStringer.toString(this, DEFAULT_FORMAT);
    }
}
