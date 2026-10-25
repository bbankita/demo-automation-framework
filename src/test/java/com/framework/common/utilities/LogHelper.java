package com.framework.common.utilities;

import io.restassured.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementing facade over SLF4J so framework code logs through one place.
 */
public final class LogHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger("framework");

    private LogHelper() {
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warn(String message) {
        LOGGER.warn(message);
    }

    public static void error(String message, Throwable cause) {
        LOGGER.error(message, cause);
    }

    public static void cleanupInfoLog(String message) {
        LOGGER.info("[CLEANUP] {}", message);
    }

    public static void logSendHttpRequest(Method method, String endpoint) {
        LOGGER.info("Sending HTTP {} request to endpoint: {}", method, endpoint);
    }

    public static void logDatabaseConnection(String dbUrl, String dbUser) {
        LOGGER.info("Connecting to database at URL: {}, User: {}", dbUrl, dbUser);
    }
}
