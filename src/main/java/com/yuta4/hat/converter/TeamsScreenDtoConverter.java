package com.yuta4.hat.converter;


import com.yuta4.hat.dto.TeamDto;
import com.yuta4.hat.dto.TeamsScreenDto;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    String teamName = getTeamName(team);
                    return new TeamDto(team.getId(), teamName, teamPlayers);
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return new TeamsScreenDto(teams,
                game.getOwner().getLogin(),
                game.getWatchers().stream()
                        .map(Player::getLogin)
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    private String getTeamName(Team team) {
        return team.getName() == null ? team.getId().toString() : team.getName();
    }

}
