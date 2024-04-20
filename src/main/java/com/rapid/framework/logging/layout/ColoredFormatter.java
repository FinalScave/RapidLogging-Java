package com.rapid.framework.logging.layout;

import com.rapid.framework.logging.Logger;
import com.rapid.framework.logging.LoggerConfig;

public class ColoredFormatter extends Formatter {
    private final static String M = "m";
    private final static String START = "\\033[";
    private final static String END = "\\033[0" + M;

    @Override
    public String format(LogLine logLine, String format) {
        String text = super.format(logLine, format);
        LoggerConfig config = Logger.getConfig();
        LoggerConfig.Style style = config.getLogStyle(logLine.level);
        text = START + getParams(style) + M + text + END;
        return text;
    }

    private static String getParams(LoggerConfig.Style style) {
        String colorAnsi;
        if (style.color == null) {
            colorAnsi = LoggerConfig.Color.WHITE.foregroundAnsi;
        } else {
            colorAnsi = style.color.foregroundAnsi;
        }

        String displayAnsi;
        if (style.modifier == null) {
            displayAnsi = LoggerConfig.Modifier.DEFAULT.ansi;
        } else {
            displayAnsi = style.modifier.ansi;
        }

        return displayAnsi + ";" + colorAnsi;
    }
}
