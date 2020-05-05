package com.yuta4.hat.converters;


import com.yuta4.hat.dto.SummaryDto;
import com.yuta4.hat.dto.TeamDto;
import com.yuta4.hat.entities.Game;
import org.springframework.core.convert.converter.Converter;

import java.util.Set;

public class SummaryScreenDtoConverter implements Converter<Game, SummaryDto> {

    @Override
    public SummaryDto convert(Game game) {
        Set<TeamDto> teams = TeamConverterUtil.convertToTeamDto(game);
        return new SummaryDto(game.getOwner().getLogin(),
                teams);
    }


}
