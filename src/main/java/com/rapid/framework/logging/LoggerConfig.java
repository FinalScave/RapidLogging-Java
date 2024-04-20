package com.rapid.framework.logging;

import com.rapid.framework.logging.constant.LogLevel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LoggerConfig {
    static LoggerConfig DEFAULT = new LoggerConfig();

    public enum Color {
        BLACK("30", "40"),
        RED("31", "41"),
        GREEN("32", "42"),
        YELLOW("33", "43"),
        BLUE("34", "44"),
        MAGENTA("35", "45"),
        CYAN("36", "46"),
        WHITE("37", "47"),
        ;
        public final String foregroundAnsi;
        public final String backgroundAnsi;

        Color(String foregroundAnsi, String backgroundAnsi) {
            this.foregroundAnsi = foregroundAnsi;
            this.backgroundAnsi = backgroundAnsi;
        }
    }

    public enum Modifier {
        DEFAULT(0, "0"),
        HIGHLIGHT(1, "1"),
        UNDERLINE(1 << 1, "4"),
        BLINK(1 << 2, "5"),
        REVERSE_DISPLAY(1 << 3, "7"),
        NON_BOLD(1 << 4, "22"),
        ;
        private int intValue;
        public final String ansi;

        Modifier(int modifier, String ansi) {
            this.intValue = modifier;
            this.ansi = ansi;
        }

        public Modifier or(Modifier modifier) {
            this.intValue |= modifier.intValue;
            return this;
        }

        public boolean isDefault() {
            return intValue == DEFAULT.intValue;
        }

        public boolean isHighlight() {
            return (intValue & HIGHLIGHT.intValue) != 0;
        }

        public boolean isUnderLine() {
            return (intValue & UNDERLINE.intValue) != 0;
        }

        public boolean isBlink() {
            return (intValue & BLINK.intValue) != 0;
        }

        public boolean isReverseDisplay() {
            return (intValue & REVERSE_DISPLAY.intValue) != 0;
        }

        public boolean isNonBold() {
            return (intValue & NON_BOLD.intValue) != 0;
        }

        public int getIntValue() {
            return intValue;
        }
    }

    public static class Style {
        public Color color;
        public Modifier modifier;
    }

    /**
     * 是否开启线程追踪
     */
    public boolean enableThreadRecord = false;
    /**
     * 是否开启堆栈追踪
     */
    public boolean enableStackTracing = false;
    /**
     * 是否开启多进程同步
     */
    public boolean enableMultiProcessSync = false;
    /**
     * 是否开启异步写入
     */
    public boolean enableAsync = false;
    /**
     * 是否开启染色
     */
    public boolean enableDyeing = false;
    private final Map<LogLevel, Style> logStyleMap = new HashMap<>();

    public void setLogStyle(LogLevel level, Style style) {
        logStyleMap.put(level, style);
    }

    public Style getLogStyle(LogLevel level) {
        return logStyleMap.get(level);
    }

    public static LoggerConfig createFromStream(InputStream in) throws IOException {
        return ConfigReader.readStream(in);
    }

    public static LoggerConfig createFromFile(File propertyFile) throws IOException {
        InputStream in = new FileInputStream(propertyFile);
        return ConfigReader.readStream(in);
    }

    public static LoggerConfig createFromResource(String resPath) throws IOException {
        InputStream in = LoggerConfig.class.getResourceAsStream(resPath);
        return ConfigReader.readStream(in);
    }

    private static class ConfigReader {
        private final static String THREAD_TRACING = "enableThreadRecord";
        private final static String STACK_TRACING = "enableStackTracing";
        private final static String MULTI_PROCESS_SYNC = "enableMultiProcessSync";
        private final static String ASYNC = "enableAsync";
        private final static String DYEING = "enableDyeing";
        private final static String STYLE_COLOR = "style.color";
        private final static String STYLE_MODIFIER = "style.modifier";

        static LoggerConfig readStream(InputStream in) throws IOException {
            LoggerConfig config = new LoggerConfig();
            Properties properties = new Properties();
            properties.load(in);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = entry.getKey().toString();
                switch (key) {
                    case THREAD_TRACING:
                        config.enableThreadRecord = Boolean.parseBoolean(entry.getValue().toString());
                        continue;
                    case STACK_TRACING:
                        config.enableStackTracing = Boolean.parseBoolean(entry.getValue().toString());
                        continue;
                    case MULTI_PROCESS_SYNC:
                        config.enableMultiProcessSync = Boolean.parseBoolean(entry.getValue().toString());
                        continue;
                    case ASYNC:
                        config.enableAsync = Boolean.parseBoolean(entry.getValue().toString());
                        continue;
                    case DYEING:
                        config.enableDyeing = Boolean.parseBoolean(entry.getValue().toString());
                        continue;
                }
                if (key.startsWith(STYLE_COLOR)) {
                    String levelString = key.substring(STYLE_COLOR.length() + 1);
                    LogLevel level = getLevel(levelString);
                    Style style = getOrPutStyle(config, level);
                    style.color = getColor(entry.getValue().toString());
                } else if (key.startsWith(STYLE_MODIFIER)) {
                    String levelString = key.substring(STYLE_MODIFIER.length() + 1);
                    LogLevel level = getLevel(levelString);
                    String value = ((String) entry.getValue()).trim();
                    Modifier modifier;
                    if (value.contains("|")) {
                        modifier = Modifier.DEFAULT;
                        String[] strings = value.split("\\|");
                        for (String string : strings) {
                            modifier.or(getModifier(string));
                        }
                    } else {
                        modifier = getModifier(value);
                    }
                    Style style = getOrPutStyle(config, level);
                    style.modifier = modifier;
                }
            }
            return config;
        }

        static Style getOrPutStyle(LoggerConfig config, LogLevel level) {
            Style style = config.getLogStyle(level);
            if (style == null) {
                style = new Style();
                config.setLogStyle(level, style);
            }
            return style;
        }

        static LogLevel getLevel(String text) {
            switch (text.toLowerCase()) {
                case "d":
                    return LogLevel.DEBUG;
                case "i":
                    return LogLevel.INFO;
                case "w":
                    return LogLevel.WARNING;
                case "e":
                    return LogLevel.ERROR;
                default:
                    return LogLevel.VERBOSE;
            }
        }

        static Color getColor(String text) {
            switch (text.toLowerCase()) {
                case "black":
                    return Color.BLACK;
                case "red":
                    return Color.RED;
                case "green":
                    return Color.GREEN;
                case "yellow":
                    return Color.YELLOW;
                case "blue":
                    return Color.BLUE;
                case "magenta":
                    return Color.MAGENTA;
                case "cyan":
                    return Color.CYAN;
                default:
                    return Color.WHITE;
            }
        }

        static Modifier getModifier(String text) {
            switch (text.toLowerCase()) {
                case "highlight":
                    return Modifier.HIGHLIGHT;
                case "underline":
                    return Modifier.UNDERLINE;
                case "blink":
                    return Modifier.BLINK;
                case "reverse":
                    return Modifier.REVERSE_DISPLAY;
                case "nonbold":
                    return Modifier.NON_BOLD;
                default:
                    return Modifier.DEFAULT;
            }
        }
    }
}
