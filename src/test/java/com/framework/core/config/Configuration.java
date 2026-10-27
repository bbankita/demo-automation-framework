package com.framework.core.config;

import com.framework.core.exceptions.FrameworkException;
import lombok.Getter;

import java.util.Map;

/**
 * Typed view of config/&lt;env&gt;.json. Populated by Jackson in {@link ConfigurationReader}.
 */
@Getter
public class Configuration {

    private String apiBaseUri;
    private String webUrl;
    private int timeoutSeconds;
    private Map<String, UserCredentials> users;
    private String jwtSecret;
    private Database database;

    public UserCredentials getUser(String userKey) {
        UserCredentials userCredentials = users.get(userKey);
        if (userCredentials == null) {
            throw new FrameworkException("No user '" + userKey + "' in configuration. Known users: " + users.keySet());
        }
        return userCredentials;
    }

}
