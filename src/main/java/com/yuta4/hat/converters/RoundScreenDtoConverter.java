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
        Duration turnTimeRemaining = countDurationRemaining(game);
        long turnGuessesCount = game.getWords().stream()
                .filter(w -> Boolean.TRUE.equals(w.getCurrentTurnGuessed()))
                .count();
        return new RoundScreenDto(game.getOwner().getLogin(),
                game.isAllowSkipWords(),
                teams,
                teamTurn != null ? teamTurn.getId() : null,
                playerTurn,
                round,
                game.getTurnStatus() == null ? TurnStatus.NOT_STARTED.toString()
                        : game.getTurnStatus().toString(),
                turnGuessesCount,
                turnTimeRemaining.getSeconds()
        );
    }

    private Duration countDurationRemaining(Game game) {
        //TODO: remove after investigation
        Duration turnTimeRemaining;
        switch (game.getTurnStatus()) {
            case ACTIVE:
                turnTimeRemaining = Duration.between(LocalDateTime.now(), game.getTurnEndTime());
                if (turnTimeRemaining.isNegative()) {
                    turnTimeRemaining = Duration.ofSeconds(0);
                }
                break;
            case PAUSED:
                turnTimeRemaining = game.getPausedTimeRemains();
                break;
            case APPROVING:
                turnTimeRemaining = Duration.ZERO;
                break;
            case NOT_STARTED:
            default:
                turnTimeRemaining = Duration.ofMinutes(1);
        }
        return turnTimeRemaining;
    }


}
