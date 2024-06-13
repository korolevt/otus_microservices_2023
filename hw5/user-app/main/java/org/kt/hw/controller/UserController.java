package org.kt.hw.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.controller.exception.UserNotFoundException;
import org.kt.hw.entity.User;
import org.kt.hw.model.Auth;
import org.kt.hw.model.UserDTO;
import org.kt.hw.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@RestController
//@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserRepository userRepository;
    private static Random rnd = new Random();

    //@RequestMapping(value = "/users", method = RequestMethod.GET)
    @GetMapping(value = "/users")
    public User getUser(@RequestParam(value = "username", required = false) String username,
                               @RequestParam(value = "password", required = false) String password) {
        log.debug("username: " + username);
        log.debug("password: " + password);
        User user = userRepository.findByNameEqualsAndPasswordEquals(username, password);
        if (user == null) {
            throw new UserNotFoundException(0);
        }
        //нужно проверить 500 на дашборде
        //var n = rnd.nextInt(100);
        //if (n == 10 || n == 30)
        //    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        return user;
        //log.debug("REST request getUsers");
        //return userRepository.findAll();
    }

    //@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    @GetMapping(value = "/users/{id}")
    public User getUserById(@PathVariable int id, HttpServletRequest request) {
        log.debug("REST request getUserById id="+id);

        Auth auth = getAuthData(request);
        if (auth == null || auth.getId() != id) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        //нужно проверить 500 на дашборде
        //var n = rnd.nextInt(100);
        //if (n == 10 || n == 30)
        //    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    //@RequestMapping(value = "/users", method = RequestMethod.POST)
    @PostMapping(value = "/users")
    public User createUser(@RequestBody UserDTO user) {
        log.debug("REST request createUser user:" + user);
        var newUser = new User();
        newUser.setName(user.getName());
        newUser.setPassword(user.getPassword());
        newUser.setEmail(user.getEmail());
        return userRepository.save(newUser);
    }

    //@RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable int id, @RequestBody UserDTO user, HttpServletRequest request) {
        log.debug("REST request updateUser id="+id + ", user:"+user);

        Auth auth = getAuthData(request);
        if (auth == null || auth.getId() != id) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return userRepository.findById(id)
            .map(newUser -> {
                newUser.setName(user.getName());
                newUser.setPassword(user.getPassword());
                newUser.setEmail(user.getEmail());
                return userRepository.save(newUser);
            })
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    //@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    @DeleteMapping(value = "/users/{id}")
    public void deleteUser(@PathVariable int id) {
        log.debug("REST request deleteUser id="+id);
        userRepository.deleteById(id);
    }

    private Auth getAuthData(HttpServletRequest request) {
        String authToken = request.getHeader("x-auth-token");
        if (authToken == null || authToken.isEmpty()) {
            log.debug("x-auth-token is empty");
            return null;
        }

        String authSalt = System.getenv("JWT_SECRET");
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(authSalt)).build();
        DecodedJWT decoded = verifier.verify(authToken);

        Instant now = Instant.now();
        Instant expiration = decoded.getClaim("expiration_in").asInstant();
        log.debug("now: " + now);
        log.debug("expiration: " + expiration);

        if (now.isAfter(expiration)) {
            throw new IllegalArgumentException("Token is expired");
        }

        int userId = decoded.getClaim("user_id").asInt();
        String userName = decoded.getClaim("user_name").asString();
        log.debug("userId: " + userId);
        log.debug("userName: " + userName);
        return new Auth(userId, userName);
    }
}
