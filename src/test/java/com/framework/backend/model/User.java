package com.framework.backend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Conduit registration payload, sent wrapped as {@code {"user": {...}}}. Field names match the API's JSON exactly.
 * Null fields are left out of the JSON, so a "missing field" payload really omits the field.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@ToString
public class User {

    private String username;
    private String email;
    @ToString.Exclude
    private String password;

    public static Builder builder() {
        return new Builder();
    }

    /** Copy of this user that can be modified without touching the original. */
    public Builder toBuilder() {
        return new Builder()
                .username(username)
                .email(email)
                .password(password);
    }

    public static final class Builder {
        private final User user = new User();

        public Builder username(String username) {
            user.username = username;
            return this;
        }

        public Builder email(String email) {
            user.email = email;
            return this;
        }

        public Builder password(String password) {
            user.password = password;
            return this;
        }

        public User build() {
            return user;
        }
    }
}
