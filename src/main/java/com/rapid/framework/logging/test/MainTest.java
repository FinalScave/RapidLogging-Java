package com.rapid.framework.logging.test;

import com.rapid.framework.logging.Logger;
import com.rapid.framework.logging.LoggerConfig;
import com.rapid.framework.logging.constant.LogLevel;

public class MainTest {
    final static Logger logger = Logger.instance(MainTest.class);

    public static void main(String[] args) {
        LoggerConfig config = new LoggerConfig();
        config.enableDyeing = true;
        config.enableStackTracing = true;

        LoggerConfig.Style style = new LoggerConfig.Style();
        style.color = LoggerConfig.Color.RED;
        style.modifier = LoggerConfig.Modifier.BLINK;
        config.setLogStyle(LogLevel.INFO, style);
        Logger.setConfig(config);

        logger.i("Hello");
    }
}
