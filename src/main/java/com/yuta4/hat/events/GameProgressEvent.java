package com.yuta4.hat.events;

import com.yuta4.hat.entities.Game;
import org.springframework.context.ApplicationEvent;

public class GameProgressEvent extends ApplicationEvent {
    public GameProgressEvent(Game game) {
        super(game);
    }
}
