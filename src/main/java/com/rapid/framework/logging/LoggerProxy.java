package com.rapid.framework.logging;

import com.rapid.framework.logging.appender.Appender;
import com.rapid.framework.logging.appender.SystemAppender;
import com.rapid.framework.logging.constant.LogLevel;
import com.rapid.framework.logging.layout.LogLine;

class LoggerProxy {
    private final static String PKG_LOGGING_INTERNAL = "com.rapid.framework.logging";
    private final static String PKG_JAVA_LANG = "java.";
    private final static String PKG_DALVIK = "dalvik.";
    final static LoggerProxy INSTANCE = new LoggerProxy();
    static Appender appender = SystemAppender.INSTANCE;
    static LogLevel level = LogLevel.VERBOSE;

    public void v(String tag, String message) {
        tryAppend(LogLevel.VERBOSE, tag, message);
    }

    public void d(String tag, String message) {
        tryAppend(LogLevel.DEBUG, tag, message);
    }

    public void i(String tag, String message) {
        tryAppend(LogLevel.INFO, tag, message);
    }

    public void w(String tag, String message) {
        tryAppend(LogLevel.WARNING, tag, message);
    }

    public void e(String tag, String message) {
        tryAppend(LogLevel.ERROR, tag, message);
    }

    public void e(String tag, Throwable e) {
        tryAppend(LogLevel.ERROR, tag, e.getMessage());
    }

    private void tryAppend(LogLevel level, String tag, String message) {
        if (appender == null) {
            return;
        }
        if (level.getValue() < LoggerProxy.level.getValue()) {
            return;
        }
        String threadName = null;
        if (Logger.getConfig().enableThreadRecord) {
            threadName = Thread.currentThread().getName();
        }
        StackTraceElement[] elements = null;
        if (Logger.getConfig().enableStackTracing) {
            elements = Thread.currentThread().getStackTrace();
        }
        LogLine logLine = makeLogLine(threadName, elements, level, tag, message);
        appender.append(logLine);
    }

    public static LogLine makeLogLine(
            String threadName,
            StackTraceElement[] stackTraceElements,
            LogLevel level,
            String tag,
            String message
    ) {
        LogLine logLine = new LogLine(System.currentTimeMillis(), level, tag, message);
        logLine.threadName = threadName;
        if (stackTraceElements != null) {
            for (StackTraceElement element : stackTraceElements) {
                String className = element.getClassName();
                if (className.startsWith(PKG_LOGGING_INTERNAL)
                        || className.startsWith(PKG_JAVA_LANG)
                        || className.startsWith(PKG_DALVIK)) {
                    continue;
                }
                // 只记录一行调用栈
                logLine.className = className;
                logLine.methodName = element.getMethodName();
                logLine.sourceFile = element.getFileName();
                logLine.sourceLine = element.getLineNumber();
                break;
            }
        }
        return logLine;
    }
}
