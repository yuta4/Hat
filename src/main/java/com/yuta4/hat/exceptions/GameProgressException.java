package com.yuta4.hat.exceptions;

import com.yuta4.hat.GameProgress;

public class GameProgressException extends RuntimeException {

    public GameProgressException(GameProgress requestedGameProgress, String error) {
        super(String.format("Can't move to %s because of the error : %s", requestedGameProgress.toString(), error));
    }

}
