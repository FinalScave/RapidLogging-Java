package com.rapid.framework.logging.appender;

import com.rapid.framework.logging.layout.LogLine;

/**
 * 输出器
 */
public interface Appender {
    void append(LogLine logLine);
}
