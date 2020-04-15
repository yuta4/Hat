package com.yuta4.hat.components;

import com.yuta4.hat.events.NewGameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Component
public class NewGameListener implements ApplicationListener<NewGameEvent>  {

    Logger logger = LoggerFactory.getLogger(NewGameListener.class);

    private final BlockingQueue<NewGameEvent> queue;

    public NewGameListener(BlockingQueue<NewGameEvent> queue) {
        this.queue = queue;
    }

    @Override
    public void onApplicationEvent(@NonNull NewGameEvent newGameEvent) {
        logger.error("NewGameListener onApplicationEvent {}", newGameEvent.getSource());
        this.queue.clear();
        this.queue.offer(newGameEvent);
    }
}
