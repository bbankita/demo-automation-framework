package com.framework.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.framework.core.exceptions.FrameworkException;

public class JwtTokenParser {

    private JwtTokenParser() {
    }

    public static DecodedJWT decode(String token){
        try{
            return JWT.decode(token);
        } catch (JWTDecodeException e){
            throw new FrameworkException("JWT token is not decodable: " + token, e);
        }
    }
}
