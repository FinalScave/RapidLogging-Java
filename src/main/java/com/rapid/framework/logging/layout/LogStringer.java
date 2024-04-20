package com.rapid.framework.logging.layout;

import com.rapid.framework.logging.Logger;

public final class LogStringer {
    public static String toString(LogLine logLine, String format) {
        Formatter formatter;
        if (Logger.getConfig().enableDyeing) {
            formatter = new ColoredFormatter();
        } else {
            formatter = new Formatter();
        }
        return formatter.format(logLine, format);
    }
}
