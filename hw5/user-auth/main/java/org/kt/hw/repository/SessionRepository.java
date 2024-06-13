package org.kt.hw.repository;

import java.time.Instant;
import java.util.UUID;

import org.kt.hw.entity.Session;
import org.kt.hw.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SessionRepository extends JpaRepository<Session, String> {


    public static Session createSession(User user) {
        return new Session(
                UUID.randomUUID(),
                user.getId(),
                user.getName(),
                Instant.now().plusSeconds(10 * 60)
        );
    }

    Session findSessionById(UUID sessionId);
}

