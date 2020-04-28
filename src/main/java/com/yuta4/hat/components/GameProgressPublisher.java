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

    private static final Logger logger = LoggerFactory.getLogger(GameProgressPublisher.class);

    private final Executor executor;
    private final BlockingQueue<GameProgressEvent> queue;

    public GameProgressPublisher(@Qualifier("gameProgressEventsExecutor") Executor executor, BlockingQueue<GameProgressEvent> queue) {
        this.executor = executor;
        this.queue = queue;
    }

    @Override
    public void accept(FluxSink<ServerSentEvent<Map<String, Object>>> sink) {
        logger.error("GameProgress publisher accepted");
        this.executor.execute(() -> {
            while (true)
                try {
                    logger.error("GameProgress publisher before taking");
                    Game game = (Game) queue.take().getSource();
                    ServerSentEvent<Map<String, Object>> sse = ServerSentEvent.<Map<String, Object>>builder()
                            .id(LocalDateTime.now().toString())
                            .event("gameProgress " + game.getId())
                            .data(game.getGameProgress().getData(game))
                            .build();
                    logger.error("GameProgress publisher took " + sse.toString());
                    sink.next(sse);
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                } catch (Throwable t) {
                    logger.error("GameProgress publisher next throw" + t.getMessage());
                }
        });
    }
}
