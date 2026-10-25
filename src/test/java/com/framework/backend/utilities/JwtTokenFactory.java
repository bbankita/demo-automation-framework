package com.framework.backend.utilities;

import com.auth0.jwt.JWT;

import java.time.Duration;
import java.time.Instant;
import com.auth0.jwt.algorithms.Algorithm;
import com.framework.exceptions.FrameworkException;

import static com.framework.common.utilities.ConfigurationReader.getConfigurations;

public class JwtTokenFactory {

    private JwtTokenFactory(){
        // Private constructor to prevent instantiation
    }

    public static String expiredToken(String email){
        Instant issuedAt = Instant.now().minus(Duration.ofDays(8));
        return JWT.create()
                .withClaim("email", email)
                .withIssuedAt(issuedAt)
                .withExpiresAt(issuedAt.plus(Duration.ofDays(7)))
                .sign(Algorithm.HMAC256(secret()));
    }

    private static String secret() {
        String secret = getConfigurations().getJwtSecret();
        if(secret == null){
            throw new FrameworkException("JWT secret is not configured. Please set");
        }
        return secret;
    }

}
