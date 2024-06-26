package org.kt.hw.repository;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.kt.hw.entity.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, String> {
    // Создать новую сессию (1 час)
    public static Session createSession(int userId, String userAgent, String ipAddress) {
        return new Session(
                null,
                userId,
                UUID.randomUUID(), // generate session uuid
                userAgent,
                ipAddress,
                Instant.now().plus(1, ChronoUnit.HOURS),
                Instant.now()
        );
    }

    // Создать новый токен (15 минут)
    public static String createAccessToken(User user) {
        return JWT.create()
                .withClaim("id", user.getId())
                .withClaim("firstName", user.getFirstName())
                .withClaim("lastName", user.getLastName())
                .withClaim("email", user.getEmail())
                .withClaim("expires", Instant.now().plus(15, ChronoUnit.MINUTES).getEpochSecond())
                .sign(Algorithm.HMAC256(System.getenv("JWT_SECRET")));
    }


//    Session findSessionById(UUID sessionId);
}

