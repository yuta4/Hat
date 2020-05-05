package com.yuta4.hat.converters;


import com.yuta4.hat.TurnStatus;
import com.yuta4.hat.dto.RoundScreenDto;
import com.yuta4.hat.dto.TeamDto;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Team;
import org.springframework.core.convert.converter.Converter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

public class RoundScreenDtoConverter implements Converter<Game, RoundScreenDto> {

    private Integer round;

    public RoundScreenDtoConverter(Integer round) {
        this.round = round;
    }

    @Override
    public RoundScreenDto convert(Game game) {
        Set<TeamDto> teams = TeamConverterUtil.convertToTeamDto(game);

        Team teamTurn = game.getTeamTurn();
        String playerTurn = teamTurn == null ? "team turn not set" :
                teamTurn.getPlayerTurn() == null ? "player turn not set" :
                teamTurn.getPlayerTurn().getLogin();
        Duration turnTimeRemaining = game.getTurnEndTime() == null ? Duration.ofMinutes(1)
                : Duration.between(LocalDateTime.now(), game.getTurnEndTime());
        return new RoundScreenDto(game.getOwner().getLogin(),
                teams,
                teamTurn != null ? teamTurn.getId() : null,
                playerTurn,
                round,
                game.getTurnStatus() == null ? TurnStatus.NOT_STARTED.toString()
                        : game.getTurnStatus().toString(),
                turnTimeRemaining.getSeconds()
        );
    }




}
