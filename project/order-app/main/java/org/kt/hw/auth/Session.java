package org.kt.hw.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.kt.hw.model.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Session {
    public static String createAccessToken(int userId) {
        // Создать токен на 1 минуту
        return JWT.create()
                .withClaim("id", userId)
                .withClaim("expires", Instant.now().plus(1, ChronoUnit.MINUTES).getEpochSecond())
                .sign(Algorithm.HMAC256(System.getenv("JWT_SECRET")));
    }
}
