package com.yuta4.hat.configuration;

import com.yuta4.hat.events.NewGameEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class GeneralConfiguration {
    @Bean
    Executor executor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    BlockingQueue<NewGameEvent> queue() {
        return new LinkedBlockingQueue<>();
    }
}
