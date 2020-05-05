package com.yuta4.hat.components.screen.processors;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.services.GameService;

import java.util.function.Consumer;

public class GenerateWordsProcessor implements Consumer<Game> {

    private GameService gameService;

    public GenerateWordsProcessor(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void accept(Game game) {
        gameService.clearWatchers(game.getId());
    }
}
