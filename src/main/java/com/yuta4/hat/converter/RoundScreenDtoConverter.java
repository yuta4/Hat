package com.yuta4.hat.converter;


import com.yuta4.hat.dto.RoundScreenDto;
import com.yuta4.hat.dto.TeamDto;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import org.springframework.core.convert.converter.Converter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RoundScreenDtoConverter implements Converter<Game, RoundScreenDto> {

    private Integer round;

    public RoundScreenDtoConverter(Integer round) {
        this.round = round;
    }

    @Override
    public RoundScreenDto convert(Game game) {
        Set<TeamDto> teams = game.getTeams() == null ? Collections.EMPTY_SET :
                game.getTeams().stream()
                        .map(team -> {
                            Set<String> teamPlayers = team.getPlayers().stream()
                                    .map(Player::getLogin)
                                    .collect(Collectors.toCollection(LinkedHashSet::new));
                            String teamName = getTeamName(team);
                            return new TeamDto(team.getId(), teamName, teamPlayers);
                        })
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        Team teamTurn = game.getTeamTurn();
        String playerTurn = teamTurn == null ? "team turn not set" :
                teamTurn.getPlayerTurn() == null ? "player turn not set" :
                teamTurn.getPlayerTurn().getLogin();
        return new RoundScreenDto(game.getOwner().getLogin(),
                teams,
                getTeamName(teamTurn),
                playerTurn,
                round
        );
    }

    private String getTeamName(Team team) {
        if(team == null) {
            return "team turn not set";
        }
        return team.getName() == null ? team.getId().toString() : team.getName();
    }


}
