package com.yuta4.hat.components.screen.processors;

import com.yuta4.hat.entities.Game;

import java.util.function.Consumer;

public class SummaryViewProcessor implements Consumer<Game> {

    @Override
    public void accept(Game game) {
        game.getWatchers().clear();
        game.setIsActive(false);
    }

}
