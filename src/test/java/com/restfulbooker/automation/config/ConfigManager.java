package com.restfulbooker.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input =
                     ConfigManager.class
                             .getClassLoader()
                             .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "config.properties was not found"
                );
            }

            properties.load(input);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load configuration",
                    e
            );
        }
    }

    private ConfigManager() {
    }

    public static String getBaseUrl() {

        return System.getProperty(
                "baseUrl",
                properties.getProperty("base.url")
        );
    }

    public static boolean isHeadless() {

        return Boolean.parseBoolean(
                System.getProperty(
                        "headless",
                        properties.getProperty("headless", "true")
                )
        );
    }
}