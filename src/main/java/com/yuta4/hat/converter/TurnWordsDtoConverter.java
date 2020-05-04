package com.yuta4.hat.converter;

import com.yuta4.hat.dto.TurnWordDto;
import com.yuta4.hat.entities.GameWord;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TurnWordsDtoConverter implements Converter<List<GameWord>, Set<TurnWordDto>> {

    @Override
    public Set<TurnWordDto> convert(List<GameWord> gameWordsList) {
        return gameWordsList.stream()
                .map(gameWord -> new TurnWordDto(gameWord.getWord().getString(),
                        gameWord.getCurrentTurnGuessed()))
                .collect(Collectors.toSet());
    }
}
