package com.yuta4.hat.converters;

import com.yuta4.hat.dto.TurnAvailableWordsDto;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.services.GameService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TurnAvailableWordsDtoConverter implements Converter<Game, TurnAvailableWordsDto> {

    private GameService gameService;

    public TurnAvailableWordsDtoConverter(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public TurnAvailableWordsDto convert(Game game) {
        return new TurnAvailableWordsDto(gameService.getAvailableWords(game, true),
                game.getTurnCurrentGuessing() != null ? game.getTurnCurrentGuessing().getWord().getString() : null);
    }

}
