package com.yuta4.hat.components.screen.processors;

import com.yuta4.hat.entities.Game;

import java.util.function.Consumer;

public class NextRoundsProcessor implements Consumer<Game> {

    @Override
    public void accept(Game game) {
        game.getWords()
                .forEach(w -> w.setTeam(null));
    }

}
