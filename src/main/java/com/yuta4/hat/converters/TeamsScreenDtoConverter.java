package com.yuta4.hat.converters;


import com.yuta4.hat.dto.TeamDto;
import com.yuta4.hat.dto.TeamsScreenDto;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import org.springframework.core.convert.converter.Converter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamsScreenDtoConverter implements Converter<Game, TeamsScreenDto> {

    @Override
    public TeamsScreenDto convert(Game game) {
        Set<TeamDto> teams = TeamConverterUtil.convertToTeamDto(game);
        return new TeamsScreenDto(teams,
                game.getOwner().getLogin(),
                game.getWatchers().stream()
                        .map(Player::getLogin)
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

}
