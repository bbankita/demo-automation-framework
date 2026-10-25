package com.framework.frontend.utilities;

import com.framework.exceptions.UnsupportedBrowserTypeException;

/**
 * Maps a browser name to its {@link DriverManager}. Stateless: the scenario's browser is owned by {@link BrowserSession}.
 */
public final class DriverManagerFactory {

    private DriverManagerFactory() {
    }

    public static DriverManager create(String browser) {
        switch (browser) {
            case "chrome":
                return new ChromeDriverManager();
            case "firefox":
                return new FirefoxDriverManager();
            default:
                throw new UnsupportedBrowserTypeException(browser);
        }
    }
}
