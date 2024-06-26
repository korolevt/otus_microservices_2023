package org.kt.hw.controller;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.kt.hw.entity.User;
import org.kt.hw.model.RegisterResponse;
import org.kt.hw.model.RegisterRequest;
import org.kt.hw.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class RegisterController {

    private final UserRepository userRepository;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is empty");
        }
        if (userRepository.findByLoginEquals(user.getLogin()) != null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Login is already exists");
        }

        User newUser = UserRepository.createUser(user);
        userRepository.save(newUser);
        createBillingAccount(newUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(newUser.getId(), newUser.getPassword()));
    }


    private static void createBillingAccount(Integer ownerId) {
        String endpoint = System.getenv("BILLING_SERVICE") + "/accounts";

        log.debug("endpoint: " + endpoint);

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("ownerId", ownerId);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost(endpoint);
            httpPost.setEntity(new StringEntity(jsonData.toString()));
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");

            int statusCode = httpClient.execute(httpPost).getStatusLine().getStatusCode();
            if (statusCode != 201) {
                throw new RuntimeException("Account was not created");
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
