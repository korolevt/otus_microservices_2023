package org.kt.hw.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.kt.hw.model.User;

public class Auth {
    // Get bearers, parses token and passes User if token exists
    public static User preHandle(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || authorization.isEmpty()) {
            return null;
        }

        String tokenString = authorization.split("Bearer ")[1];
        try {
            Algorithm algorithm = Algorithm.HMAC256(System.getenv("JWT_SECRET"));
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(tokenString);

            User user = new User();
            user.setId(decodedJWT.getClaim("id").asInt());
            user.setFirstName(decodedJWT.getClaim("firstName").asString());
            user.setLastName(decodedJWT.getClaim("lastName").asString());
            user.setEmail(decodedJWT.getClaim("email").asString());

            return user;
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
