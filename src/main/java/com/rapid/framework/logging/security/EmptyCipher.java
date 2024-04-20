package com.rapid.framework.logging.security;

public class EmptyCipher implements Cipher {
    public final static EmptyCipher INSTANCE = new EmptyCipher();

    private EmptyCipher() {
    }

    @Override
    public String encode(String content) {
        return content;
    }

    @Override
    public String decode(String content) {
        return content;
    }
}
