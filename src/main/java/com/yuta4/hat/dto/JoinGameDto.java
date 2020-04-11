package com.yuta4.hat.dto;

import com.yuta4.hat.entities.Game;
import lombok.Getter;

@Getter
public class JoinGameDto {

    public JoinGameDto(Game game) {
        gameId = game.getId();
        login = game.getOwner().getLogin();
    }

    private Long gameId;
    private String login;
}
