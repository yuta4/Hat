package com.yuta4.hat.configuration;

import com.yuta4.hat.events.GameProgressEvent;
import com.yuta4.hat.events.NewGameEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class GeneralConfiguration {
    @Bean
    @Qualifier("newGamesEventExecutor")
    Executor newGamesEventExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    @Qualifier("gameProgressEventsExecutor")
    Executor gameProgressEventsExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    BlockingQueue<NewGameEvent> newGamesEventQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    BlockingQueue<GameProgressEvent> gameProgressEventsQueue() {
        return new LinkedBlockingQueue<>();
    }
}
