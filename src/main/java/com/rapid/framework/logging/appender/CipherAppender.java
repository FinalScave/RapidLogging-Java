package com.rapid.framework.logging.appender;

import com.rapid.framework.logging.security.Cipher;

public interface CipherAppender extends Appender {
    void setCipher(Cipher cipher);
}
