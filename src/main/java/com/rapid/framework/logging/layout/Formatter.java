package com.rapid.framework.logging.layout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Formatter {
    @SuppressWarnings("SimpleDateFormat")
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public final static char FORMAT_IDT = '%';
    public final static char TIME = 'd';
    public final static char THREAD = 't';
    public final static char LEVEL = 'l';
    public final static char TAG = 'i';
    public final static char MESSAGE = 's';
    public final static char CLASS_NAME = 'c';
    public final static char METHOD_NAME = 'm';
    public final static char SOURCE_FILE = 'o';
    public final static char SOURCE_LINE = 'n';

    public String format(LogLine logLine, String format) {
        StringBuilder builder = new StringBuilder();
        char[] chars = format.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch == FORMAT_IDT) {
                if (i < chars.length - 1) {
                    char next = chars[i + 1];
                    if (Character.isLetter(next)) {
                        builder.append(getPartial(logLine, next));
                        i++;
                    } else {
                        builder.append(ch);
                    }
                }
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    public String getPartial(LogLine logLine, char letter) {
        switch (Character.toLowerCase(letter)) {
            case TIME:
                return DATE_FORMAT.format(logLine.timestamp) + makeSpace(logLine.spaceAfterTime);
            case THREAD:
                return logLine.threadName + makeSpace(logLine.spaceAfterThreadName);
            case LEVEL:
                return logLine.level.getLetter() + makeSpace(logLine.spaceAfterLevel);
            case TAG:
                return logLine.tag + makeSpace(logLine.spaceAfterTag);
            case MESSAGE:
                return logLine.message + makeSpace(logLine.spaceAfterMessage);
            case CLASS_NAME:
                return logLine.className + makeSpace(logLine.spaceAfterClassName);
            case METHOD_NAME:
                return logLine.methodName + makeSpace(logLine.spaceAfterMethodName);
            case SOURCE_FILE:
                return logLine.sourceFile + makeSpace(logLine.spaceAfterSourceFile);
            case SOURCE_LINE:
                return String.valueOf(logLine.sourceLine) + makeSpace(logLine.spaceAfterSourceLine);
            default:
                return String.valueOf(letter);
        }
    }

    private String makeSpace(int n) {
        if (n == 0) {
            return "";
        }
        String spaces = "";
        for (int i = 0; i < n; i++) {
            spaces += " ";
        }
        return spaces;
    }
}
