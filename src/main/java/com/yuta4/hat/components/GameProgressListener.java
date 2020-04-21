package com.yuta4.hat.components;

import com.yuta4.hat.events.GameProgressEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Component
public class GameProgressListener implements ApplicationListener<GameProgressEvent> {

    Logger logger = LoggerFactory.getLogger(GameProgressListener.class);

    private final BlockingQueue<GameProgressEvent> queue;

    public GameProgressListener(BlockingQueue<GameProgressEvent> queue) {
        this.queue = queue;
    }

    @Override
    public void onApplicationEvent(@NonNull GameProgressEvent gameProgressEvent) {
        logger.error("GameProgressListener onApplicationEvent {}", gameProgressEvent.getSource());
        this.queue.offer(gameProgressEvent);
    }
}