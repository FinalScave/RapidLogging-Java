package com.rapid.framework.logging.constant;

public enum LogLevel {
    VERBOSE((byte) 0, "V"),
    DEBUG((byte) 1, "D"),
    INFO((byte) 2, "I"),
    WARNING((byte) 3, "W"),
    ERROR((byte) 4, "E"),
    ;
    private final byte value;
    private final String letter;

    LogLevel(byte value, String letter) {
        this.value = value;
        this.letter = letter;
    }

    public byte getValue() {
        return value;
    }

    public String getLetter() {
        return letter;
    }

    public static LogLevel fromValue(byte levelValue) {
        switch (levelValue) {
            case 1:
                return DEBUG;
            case 2:
                return INFO;
            case 3:
                return WARNING;
            case 4:
                return ERROR;
            default:
                return VERBOSE;
        }
    }
}
