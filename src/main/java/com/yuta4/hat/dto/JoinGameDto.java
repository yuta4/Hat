package com.yuta4.hat.dto;

import com.yuta4.hat.entities.Game;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class JoinGameDto {

    public JoinGameDto(Game game) {
        gameId = game.getId();
        login = game.getOwner().getLogin();
    }

    private final Long gameId;
    private final String login;
}
