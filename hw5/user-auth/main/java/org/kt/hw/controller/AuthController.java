package org.kt.hw.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kt.hw.entity.Session;
import org.kt.hw.repository.SessionRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
//@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SessionRepository sessionRepository;


    @GetMapping("/")
    public ResponseEntity<Void> auth(HttpServletRequest request) {
        Cookie sessionCookie = getSessionCookie(request);
        if (sessionCookie == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Session session = sessionRepository.findSessionById(UUID.fromString(sessionCookie.getValue()));
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = JWT.create()
                .withClaim("user_id", session.getUserId())
                .withClaim("user_name", session.getUserName())
                .withClaim("expiration_in", session.getExpiresIn())
                .sign(Algorithm.HMAC256(System.getenv("JWT_SECRET")));

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-auth-token", token);
        return ResponseEntity.ok().headers(headers).build();
    }

    private Cookie getSessionCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("session".equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }
}

