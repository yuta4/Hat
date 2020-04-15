package com.yuta4.hat.events;

import com.yuta4.hat.entities.Player;
import org.springframework.context.ApplicationEvent;

public class NewGameEvent extends ApplicationEvent {
    public NewGameEvent(Player owner) {
        super(owner);
    }
}
