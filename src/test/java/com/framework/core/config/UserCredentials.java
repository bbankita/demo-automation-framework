package com.framework.core.config;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserCredentials {

    private String username;
    @ToString.Exclude
    private String password;
    private String email;

}
