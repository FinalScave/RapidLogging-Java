package com.rapid.framework.logging.security;

public interface Cipher {
    /**
     * 加密日志内容
     * @param content 欲加密日志内容
     * @return 加密后的日志内容
     */
    String encode(String content);

    /**
     * 解密日志内容
     * @param content 欲解密的日志内容
     * @return 解密后的日志内容
     */
    String decode(String content);
}
