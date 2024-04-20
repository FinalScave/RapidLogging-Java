package com.rapid.framework.logging;

import com.rapid.framework.logging.constant.LogLevel;
import com.rapid.framework.logging.layout.LogLine;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

class AsyncLoggerProxy extends LoggerProxy {
    final static AsyncLoggerProxy INSTANCE = new AsyncLoggerProxy();
    static ExecutorService task = Executors.newFixedThreadPool(2);
    Queue<LogLine> queue = new LinkedList<>();
    AtomicBoolean canceled = new AtomicBoolean(false);

    @Override
    public void v(String tag, String message) {
        tryAppend(LogLevel.VERBOSE, tag, message);
    }

    @Override
    public void d(String tag, String message) {
        tryAppend(LogLevel.DEBUG, tag, message);
    }

    @Override
    public void i(String tag, String message) {
        tryAppend(LogLevel.INFO, tag, message);
    }

    @Override
    public void w(String tag, String message) {
        tryAppend(LogLevel.WARNING, tag, message);
    }

    @Override
    public void e(String tag, String message) {
        tryAppend(LogLevel.ERROR, tag, message);
    }

    @Override
    public void e(String tag, Throwable e) {
        tryAppend(LogLevel.ERROR, tag, e.getMessage());
    }

    protected void loop() {
        canceled.set(false);
        task.execute(() -> {
            while (!canceled.get()) {
                if (appender == null) {
                    continue;
                }
                LogLine logLine = queue.poll();
                if (logLine == null) {
                    continue;
                }
                appender.append(logLine);
            }
        });
    }

    protected void cancel() {
        canceled.compareAndSet(false, true);
    }

    private void tryAppend(LogLevel level, String tag, String message) {
        if (level.getValue() < LoggerProxy.level.getValue()) {
            return;
        }
        String threadName;
        if (Logger.getConfig().enableThreadRecord) {
            threadName = Thread.currentThread().getName();
        } else {
            threadName = null;
        }
        StackTraceElement[] elements;
        if (Logger.getConfig().enableStackTracing) {
            elements = Thread.currentThread().getStackTrace();
        } else {
            elements = null;
        }
        task.execute(() -> {
            LogLine logLine = LoggerProxy.makeLogLine(threadName, elements, level, tag, message);
            queue.add(logLine);
        });
    }
}
