package org.kt.hw.controller.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(int id) {
        super("Could not find user " + id);
    }
}