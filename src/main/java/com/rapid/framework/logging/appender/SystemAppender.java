package com.rapid.framework.logging.appender;

import com.rapid.framework.logging.constant.LogLevel;
import com.rapid.framework.logging.layout.LogLine;

public final class SystemAppender implements Appender {
    public final static SystemAppender INSTANCE = new SystemAppender();

    private SystemAppender() { }

    @Override
    public void append(LogLine logLine) {
        if (logLine.level == LogLevel.ERROR) {
            System.err.println(logLine);
        } else {
            System.out.println(logLine);
        }
    }
}
