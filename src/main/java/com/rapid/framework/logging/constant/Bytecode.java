package com.rapid.framework.logging.constant;

import com.rapid.framework.logging.layout.LogLine;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Bytecode {
    @ByteLength(Length.U4)
    int MAGIC = 0xff110099;
    @ByteLength(Length.U2)
    short MAJOR_VERSION_1 = 0x1;
    @ByteLength(Length.U2)
    short MINOR_VERSION_0 = 0x0;
    @ByteLength(Length.U1)
    byte TAG_POOL = 0x1;
    @ByteLength(Length.U1)
    byte TAG_LOG = 0x2;
    /**
     * 文件头+版本的字长
     * <ul>
     *     <li>{@link Bytecode#MAGIC} - 4 bytes</li>
     *     <li>{@link Bytecode#MAJOR_VERSION_1} - 2 bytes</li>
     *     <li>{@link Bytecode#MINOR_VERSION_0} - 2 bytes</li>
     * </ul>
     */
    @ByteLength(Length.U4)
    int LENGTH_HEAD_V1 = 4 + 2 + 2;
    /**
     * 日志块的字长
     * <ul>
     *     <li>{@link LogLine#timestamp} - 8 bytes</li>
     *     <li>{@link LogLine#level} - 1 byte(枚举以[0, 127]之间的数字存储，即byte)</li>
     *     <li>{@link LogLine#tag} - 4 bytes(字符串常量ID)</li>
     *     <li>{@link LogLine#message} - 4 bytes(字符串常量ID)</li>
     *     <li>{@link LogLine#threadName} - 4 bytes(字符串常量ID)</li>
     *     <li>{@link LogLine#className} - 4 bytes(字符串常量ID)</li>
     *     <li>{@link LogLine#methodName} - 4 bytes(字符串常量ID)</li>
     *     <li>{@link LogLine#sourceFile} - 4 bytes(字符串常量ID)</li>
     *     <li>{@link LogLine#sourceLine} - 4 bytes(行号以整数格式存储)</li>
     * </ul>
     */
    @ByteLength(Length.U4)
    int LENGTH_LOG_V1 = 8 + 1 + 4 * 7;

    enum Length {
        U1(1),
        U2(2),
        U4(4),
        U8(8);
        final int value;

        Length(int value) {
            this.value = value;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @interface ByteLength {
        Length value();
    }
}
