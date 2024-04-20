package com.rapid.framework.logging;

import com.rapid.framework.logging.appender.Appender;
import com.rapid.framework.logging.constant.LogLevel;
import com.rapid.framework.logging.lock.LogMultiThreadSync;

/**
 * 日志输出器
 */
public final class Logger {
    private static LoggerProxy proxy = LoggerProxy.INSTANCE;
    private final LogMultiThreadSync threadSync = LogMultiThreadSync.INSTANCE;
    private final String tag;
    private static LoggerConfig config = LoggerConfig.DEFAULT;

    private Logger(String tag) {
        this.tag = tag;
    }

    public static Logger instance(Class<?> klass) {
        return instance(klass.getSimpleName());
    }

    public static Logger instance(String tag) {
        return new Logger(tag);
    }

    public static void setLevel(LogLevel level) {
        LoggerProxy.level = level;
    }

    public static void setAppender(Appender appender)
            throws IllegalArgumentException, IllegalStateException {
        if (appender == null) {
            throw new IllegalArgumentException("appender cannot be null");
        }
        LoggerProxy.appender = appender;
    }

    public static void setConfig(LoggerConfig config) {
        if (config == null) {
            config = LoggerConfig.DEFAULT;
        }
        if (config.enableAsync) {
            proxy = AsyncLoggerProxy.INSTANCE;
            AsyncLoggerProxy.INSTANCE.loop();
        } else {
            proxy = LoggerProxy.INSTANCE;
            AsyncLoggerProxy.INSTANCE.cancel();
        }
        Logger.config = config;
    }

    public static LoggerConfig getConfig() {
        return config;
    }

    public void v(String message) {
        threadSync.lock();
        proxy.v(tag, message);
        threadSync.unlock();
    }

    public void d(String message) {
        threadSync.lock();
        proxy.d(tag, message);
        threadSync.unlock();
    }

    public void i(String message) {
        threadSync.lock();
        proxy.i(tag, message);
        threadSync.unlock();
    }

    public void w(String message) {
        threadSync.lock();
        proxy.w(tag, message);
        threadSync.unlock();
    }

    public void e(String message) {
        threadSync.lock();
        proxy.e(tag, message);
        threadSync.unlock();
    }

    public void e(Throwable e) {
        threadSync.lock();
        proxy.e(tag, e);
        threadSync.unlock();
    }
}
