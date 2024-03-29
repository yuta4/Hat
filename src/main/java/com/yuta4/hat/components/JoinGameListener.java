package com.yuta4.hat.components;

import com.yuta4.hat.events.NewGameEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Component
@Slf4j
public class JoinGameListener implements ApplicationListener<NewGameEvent>  {

    private static final Logger logger = LoggerFactory.getLogger(JoinGameListener.class);

    private final BlockingQueue<NewGameEvent> queue;

    public JoinGameListener(BlockingQueue<NewGameEvent> queue) {
        this.queue = queue;
    }

    @Override
    public void onApplicationEvent(@NonNull NewGameEvent newGameEvent) {
        logger.error("NewGameListener onApplicationEvent {}", newGameEvent.getSource());
        this.queue.offer(newGameEvent);
    }
}
