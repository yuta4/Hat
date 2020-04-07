package com.yuta4.hat.exceptions;

public class NoSuchGameException extends RuntimeException {
    public NoSuchGameException(String message) {
        super("Can't find the game with id " + message);
    }
}
