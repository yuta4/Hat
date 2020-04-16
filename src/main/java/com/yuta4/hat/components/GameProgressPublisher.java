package com.yuta4.hat.components;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.events.GameProgressEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Slf4j
@Component
public class GameProgressPublisher implements
        Consumer<FluxSink<ServerSentEvent<Map<String, Object>>>> {

    Logger logger = LoggerFactory.getLogger(GameProgressPublisher.class);

    private final Executor executor;
    private final BlockingQueue<GameProgressEvent> queue;

    public GameProgressPublisher(@Qualifier("gameProgressEventsExecutor") Executor executor, BlockingQueue<GameProgressEvent> queue) {
        this.executor = executor;
        this.queue = queue;
    }

    @Override
    public void accept(FluxSink<ServerSentEvent<Map<String, Object>>> sink) {
        logger.error("Join game publisher accepted");
        this.executor.execute(() -> {
            while (true)
                try {
                    logger.error("Join game publisher before taking");
                    Game game = (Game) queue.take().getSource();
                    logger.error("Join game publisher took");
                    sink.next(ServerSentEvent.<Map<String, Object>>builder()
                            .id(LocalDateTime.now().toString())
                            .event(game.getId().toString())
                            .data(game.getGameProgress().getData(game))
                            .build());
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });
    }
}
