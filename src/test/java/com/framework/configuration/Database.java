package com.framework.configuration;

import lombok.Getter;

@Getter
public class Database {

    private String host;
    private String username;
    private String password;

    @Override
    public String toString() {
        // Never print the password
        return "Database{host='" + host + ", username:='"+username+ " }";
    }
}
