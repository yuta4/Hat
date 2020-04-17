package com.yuta4.hat.converter;


import com.yuta4.hat.dto.TeamDto;
import com.yuta4.hat.dto.TeamsScreenDto;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableSet;

@Component
public class TeamsScreenDtoConverter implements Converter<Game, TeamsScreenDto> {

    @Override
    public TeamsScreenDto convert(Game game) {
        Set<TeamDto> teams = game.getTeams() == null ? Collections.EMPTY_SET :
                game.getTeams().stream()
                .map(team -> {
                    Set<String> teamPlayers = team.getPlayers().stream()
                            .map(Player::getLogin)
                            .collect(toUnmodifiableSet());
                    return new TeamDto(team.getId(), team.getId().toString(), teamPlayers);
                })
                .collect(toSet());
        return new TeamsScreenDto(teams,
                game.getOwner().getLogin(),
                game.getWatchers().stream()
                        .map(Player::getLogin)
                        .collect(toUnmodifiableSet()));
    }

}
