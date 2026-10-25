package com.framework.common.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.framework.configuration.Configuration;
import com.framework.exceptions.FrameworkException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Single source of runtime settings.
 * <p>
 * File settings come from {@code config/<env>.json} on the classpath ({@code -Denv}, default {@code qa}).
 * Any string value may be written as {@code ${ENV_VAR}} or {@code ${ENV_VAR:default}}, so secrets
 * are supplied by the environment instead of being committed. Lookup order: environment variable, then a
 * git-ignored {@code .env} file in the working directory (local runs), then the inline default.
 * <p>
 * Run settings come from system properties: {@code -Dbrowser}, {@code -Dheadless}, {@code -Dtimeout}.
 */
public final class ConfigurationReader {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([A-Za-z0-9_]+)(?::([^}]*))?}");

    private final String environment = System.getProperty("env", "qa");
    private final String browserName = System.getProperty("browser", "chrome").toLowerCase();
    private final boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
    private final Configuration configuration;

    private ConfigurationReader() {
        configuration = load("config/" + environment + ".json");
    }

    /** Initialization-on-demand holder: lazy and thread-safe without synchronization. */
    private static final class Holder {
        private static final ConfigurationReader INSTANCE = new ConfigurationReader();
    }

    public static Configuration getConfigurations() {
        return Holder.INSTANCE.configuration;
    }

    public static String getEnvironment() {
        return Holder.INSTANCE.environment;
    }

    public static String getBrowserName() {
        return Holder.INSTANCE.browserName;
    }

    public static boolean isHeadless() {
        return Holder.INSTANCE.headless;
    }

    private static Configuration load(String resource) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream stream = ConfigurationReader.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new FrameworkException("Configuration file not found on classpath: " + resource);
            }
            JsonNode tree = mapper.readTree(stream);
            resolvePlaceholders(tree, readDotEnv(Paths.get(System.getProperty("user.dir"), ".env")));
            String timeoutOverride = System.getProperty("timeout");
            if (timeoutOverride != null) {
                ((ObjectNode) tree).put("timeoutSeconds", Integer.parseInt(timeoutOverride));
            }
            return mapper.treeToValue(tree, Configuration.class);
        } catch (IOException e) {
            throw new FrameworkException("Could not read configuration: " + resource, e);
        }
    }

    /** Reads {@code KEY=VALUE} lines; blank values count as unset so a copied {@code .env.example} keeps defaults. */
    private static Map<String, String> readDotEnv(Path file) {
        if (!Files.isRegularFile(file)) {
            return Collections.emptyMap();
        }
        Map<String, String> values = new HashMap<>();
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                int separator = trimmed.indexOf('=');
                if (trimmed.isEmpty() || trimmed.startsWith("#") || separator < 1) {
                    continue;
                }
                String value = trimmed.substring(separator + 1).trim();
                if (value.length() >= 2 && (value.startsWith("\"") && value.endsWith("\"")
                        || value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }
                if (!value.isEmpty()) {
                    values.put(trimmed.substring(0, separator).trim(), value);
                }
            }
        } catch (IOException e) {
            throw new FrameworkException("Could not read " + file, e);
        }
        return values;
    }

    private static void resolvePlaceholders(JsonNode node, Map<String, String> dotEnv) {
        if (!node.isObject()) {
            return;
        }
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getValue().isTextual()) {
                field.setValue(new TextNode(resolve(field.getValue().asText(), dotEnv)));
            } else {
                resolvePlaceholders(field.getValue(), dotEnv);
            }
        }
    }

    private static String resolve(String value, Map<String, String> dotEnv) {
        Matcher matcher = PLACEHOLDER.matcher(value);
        StringBuilder resolved = new StringBuilder();
        while (matcher.find()) {
            String variable = matcher.group(1);
            String fromEnv = System.getenv(variable);
            String replacement = fromEnv != null ? fromEnv : dotEnv.getOrDefault(variable, matcher.group(2));
            if (replacement == null) {
                throw new FrameworkException("Environment variable " + variable + " is required by the configuration but is not set");
            }
            matcher.appendReplacement(resolved, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(resolved);
        return resolved.toString();
    }
}
