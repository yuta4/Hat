package com.yuta4.hat.exceptionas;

public class NoSuchGameException extends RuntimeException {
    public NoSuchGameException(String message) {
        super("Can't find the game with id " + message);
    }
}
