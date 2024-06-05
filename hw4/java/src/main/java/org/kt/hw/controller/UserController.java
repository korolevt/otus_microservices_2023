package org.kt.hw.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.controller.exception.UserNotFoundException;
import org.kt.hw.entity.User;
import org.kt.hw.model.UserDTO;
import org.kt.hw.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public List<User> getUsers() {
        //нужно проверить 500 на дашборде
        var n = rnd.nextInt(100);
        if (n == 10 || n == 30)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        log.debug("REST request getUsers");
        return userRepository.findAll();
    }

    //@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    @GetMapping(value = "/users/{id}")
    public User getUserById(@PathVariable int id) {
        log.debug("REST request getUserById id="+id);
        //нужно проверить 500 на дашборде
        var n = rnd.nextInt(100);
        if (n == 10 || n == 30)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    //@RequestMapping(value = "/users", method = RequestMethod.POST)
    @PostMapping(value = "/users")
    public User createUser(@RequestBody UserDTO user) {
        log.debug("REST request createUser user:" + user);
        var newUser = new User();
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        return userRepository.save(newUser);
    }

    //@RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable int id, @RequestBody UserDTO user) {
        log.debug("REST request updateUser id="+id + ", user:"+user);
        return userRepository.findById(id)
            .map(newUser -> {
                newUser.setName(user.getName());
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

}
