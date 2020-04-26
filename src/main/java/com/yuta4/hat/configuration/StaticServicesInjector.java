package com.yuta4.hat.configuration;

import com.yuta4.hat.GameProgress;
import com.yuta4.hat.components.GameProgressValidator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StaticServicesInjector {

    private GameProgressValidator gameProgressValidator;

    public StaticServicesInjector(GameProgressValidator gameProgressValidator) {
        this.gameProgressValidator = gameProgressValidator;
    }

    @PostConstruct
    public void postConstruct() {
        GameProgress.setTeamService(gameProgressValidator);
    }
}

