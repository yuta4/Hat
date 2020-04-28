package com.yuta4.hat.components;

import com.yuta4.hat.dto.JoinGameDto;
import com.yuta4.hat.events.NewGameEvent;
import com.yuta4.hat.services.GameService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Component
public class JoinGamePublisher implements
        Consumer<FluxSink<ServerSentEvent<Set<JoinGameDto>>>> {

    private static final Logger logger = LoggerFactory.getLogger(JoinGamePublisher.class);

    private final Executor executor;
    private final BlockingQueue<NewGameEvent> queue;
    private final GameService gameService;

    public JoinGamePublisher(@Qualifier("newGamesEventExecutor") Executor executor, BlockingQueue<NewGameEvent> queue, GameService gameService) {
        this.executor = executor;
        this.queue = queue;
        this.gameService = gameService;
    }

    @Override
    public void accept(FluxSink<ServerSentEvent<Set<JoinGameDto>>> sink) {
        logger.error("Join game publisher accepted");
        this.executor.execute(() -> {
            while (true)
                try {
                    logger.error("Join game publisher before taking");
                    queue.take();
                    ServerSentEvent<Set<JoinGameDto>> sse =
                            ServerSentEvent.<Set<JoinGameDto>>builder()
                                    .id(LocalDateTime.now().toString())
                                    .event("join")
                                    .data(gameService.getNotStartedGames().stream()
                                            .map(JoinGameDto::new)
                                            .collect(toSet()))
                                    .build();
                    logger.error("Join game publisher took {}", sse);
                    sink.next(sse);
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });
    }
}
