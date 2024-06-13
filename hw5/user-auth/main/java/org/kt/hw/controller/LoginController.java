package org.kt.hw.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.entity.Session;
import org.kt.hw.model.Login;
import org.kt.hw.model.User;
import org.kt.hw.repository.SessionRepository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
//RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final SessionRepository sessionRepository;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(@RequestBody Login login, HttpServletResponse response) {
        try {
            String username = login.getUsername();
            String password = login.getPassword();

            if (username == null || username.isEmpty()) {
                ControllerHelper.errorResponse(response, "Username is empty", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (password == null || password.isEmpty()) {
                ControllerHelper.errorResponse(response, "Password is empty", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            User user = getUserByUsername(username, password);
            if (user == null) {
                ControllerHelper.errorResponse(response, "login or password is not correct", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Session newSession = SessionRepository.createSession(user);
            Cookie newSessionCookie = new Cookie("session", newSession.getId().toString());
            newSessionCookie.setPath("/");
            newSessionCookie.setMaxAge((int) (newSession.getExpiresIn().getEpochSecond() - Instant.now().getEpochSecond()));
            newSessionCookie.setHttpOnly(true);

            sessionRepository.save(newSession);

            response.addCookie(newSessionCookie);
            ControllerHelper.successResponse(response, "Authenticated!");
        } catch (Exception e) {
            ControllerHelper.errorResponse(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static User getUserByUsername(String username, String password) {
        try {
            String endpoint = String.format("%s/users?username=%s&password=%s",
                    System.getenv("USER_SERVICE"), username, password);

            log.debug("endpoint: " + endpoint);

            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                throw new IOException("internal error");
            }

            if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST ||
                responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                throw new IOException("login or password is not correct");
            }

//            String jsonObject = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //User user = new Gson().fromJson(jsonObject, User.class);

            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.readValue(connection.getInputStream(), User.class);

            if (user.getId() == 0) {
                throw new IOException("login or password is not correct");
            }

            return user;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

