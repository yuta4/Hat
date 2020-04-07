package com.yuta4.hat.exceptions;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Long gameId) {
        super("Can't find the game with id " + gameId);
    }
}
