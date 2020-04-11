package com.yuta4.hat.exceptions;

public class NoSuchPlayerException extends RuntimeException {

    public NoSuchPlayerException(String login) {
        super("Can't find the player with login " + login);
    }
}
