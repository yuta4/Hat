package com.yuta4.hat.converters;

import com.yuta4.hat.dto.TurnApprovingWordDto;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.GameWord;
import com.yuta4.hat.entities.Player;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TurnWordsDtoConverter implements Converter<List<GameWord>, Set<TurnApprovingWordDto>> {

    private final static Logger logger = LoggerFactory.getLogger(TurnWordsDtoConverter.class);

    @Override
    public Set<TurnApprovingWordDto> convert(List<GameWord> gameWordsList) {
        Set<TurnApprovingWordDto> collected = gameWordsList.stream()
                .map(gameWord -> new TurnApprovingWordDto(gameWord.getWord().getString(),
                        gameWord.getCurrentTurnGuessed()))
                .collect(Collectors.toSet());
        Game forDebug = gameWordsList.stream().map(GameWord::getGame).findFirst().orElse(null);
        Long id = forDebug != null ? forDebug.getId() : null;
        Player turn = forDebug != null && forDebug.getTeamTurn() != null ?
                forDebug.getTeamTurn().getPlayerTurn() : null;
        logger.info("Turn words set {}, {} : {}", id, turn, collected);
        return collected;
    }
}
