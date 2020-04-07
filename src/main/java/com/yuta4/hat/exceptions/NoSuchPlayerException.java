package com.yuta4.hat.exceptions;

public class NoSuchPlayerException extends RuntimeException {

    public NoSuchPlayerException(String email) {
        super("Can't find the player with email " + email);
    }
}
