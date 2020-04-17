package com.yuta4.hat.configuration;

import com.yuta4.hat.GameProgress;
import com.yuta4.hat.components.GameProgressValidator;
import com.yuta4.hat.converter.TeamsScreenDtoConverter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StaticServicesInjector {

    private TeamsScreenDtoConverter teamsScreenDtoConverter;
    private GameProgressValidator gameProgressValidator;

    public StaticServicesInjector(TeamsScreenDtoConverter teamsScreenDtoConverter, GameProgressValidator gameProgressValidator) {
        this.teamsScreenDtoConverter = teamsScreenDtoConverter;
        this.gameProgressValidator = gameProgressValidator;
    }

    @PostConstruct
    public void postConstruct() {
        GameProgress.setTeamService(teamsScreenDtoConverter, gameProgressValidator);
    }
}

