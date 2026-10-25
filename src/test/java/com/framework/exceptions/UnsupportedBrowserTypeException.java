package com.framework.exceptions;

public class UnsupportedBrowserTypeException extends FrameworkException {

    public UnsupportedBrowserTypeException(String browser) {
        super("Browser '" + browser + "' is not supported. Supported browsers: chrome, firefox");
    }
}
