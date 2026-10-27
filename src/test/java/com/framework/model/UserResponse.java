package com.framework.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @ToString.Exclude
    @JsonProperty("token")
    private String token;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("image")
    private String image;
}
