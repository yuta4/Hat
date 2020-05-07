package com.yuta4.hat.converters;

import com.yuta4.hat.dto.TurnWordDto;
import com.yuta4.hat.entities.GameWord;
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
public class TurnWordsDtoConverter implements Converter<List<GameWord>, Set<TurnWordDto>> {

    private final static Logger logger = LoggerFactory.getLogger(TurnWordsDtoConverter.class);

    @Override
    public Set<TurnWordDto> convert(List<GameWord> gameWordsList) {
        Set<TurnWordDto> collected = gameWordsList.stream()
                .map(gameWord -> new TurnWordDto(gameWord.getWord().getString(),
                        gameWord.getCurrentTurnGuessed()))
                .collect(Collectors.toSet());
        logger.info("Turn words set : {}", collected);
        return collected;
    }
}
