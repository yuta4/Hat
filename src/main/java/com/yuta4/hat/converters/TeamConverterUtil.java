package com.yuta4.hat.converters;

import com.yuta4.hat.dto.TeamDto;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamConverterUtil {

    public static Set<TeamDto> convertToTeamDto(Game game) {
        return game.getTeams() == null ? Collections.EMPTY_SET :
                game.getTeams().stream()
                        .map(team -> {
                            Set<String> teamPlayers = team.getPlayers().stream()
                                    .map(Player::getLogin)
                                    .collect(Collectors.toCollection(LinkedHashSet::new));
                            String teamName = getTeamName(team);
                            Long teamScore = team.getScore() == null ? 0 : team.getScore();
                            return new TeamDto(team.getId(), teamName, teamPlayers, teamScore);
                        })
                        .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static String getTeamName(Team team) {
        if(team == null) {
            return null;
        }
        return team.getName() == null ? "Team " + team.getId() : team.getName();
    }

}
