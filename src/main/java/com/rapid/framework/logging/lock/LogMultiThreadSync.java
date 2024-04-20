package com.rapid.framework.logging.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 日志多线程同步工具类
 */
public final class LogMultiThreadSync {
    public final static LogMultiThreadSync INSTANCE = new LogMultiThreadSync();
    private final ReentrantLock lock = new ReentrantLock();

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
