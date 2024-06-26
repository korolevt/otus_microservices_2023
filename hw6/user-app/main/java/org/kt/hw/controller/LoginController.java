package org.kt.hw.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.entity.Session;
import org.kt.hw.entity.User;
import org.kt.hw.model.LoginRequest;
import org.kt.hw.model.LoginResponse;
import org.kt.hw.repository.SessionRepository;
import org.kt.hw.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class LoginController {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest login, HttpServletRequest request) {
        if (login == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login is empty");
        }

        User actualUser = userRepository.findByLoginEqualsAndPasswordEquals(login.getLogin(), login.getPassword());
        if (actualUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Session newSession = SessionRepository.createSession(
                actualUser.getId(),
                request.getHeader("user-agent"),
                request.getRemoteAddr()
        );
        sessionRepository.save(newSession);

        String accessToken = SessionRepository.createAccessToken(actualUser);

        return ResponseEntity.ok(new LoginResponse(accessToken, newSession.getToken().toString()));
    }
}

