package com.yuta4.hat.configuration;

import com.yuta4.hat.GameProgress;
import com.yuta4.hat.components.FirstRoundProcessor;
import com.yuta4.hat.components.GameProgressValidator;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.services.GameService;
import com.yuta4.hat.services.GameWordService;
import com.yuta4.hat.services.TeamService;
import com.yuta4.hat.services.WordService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.function.Consumer;

@Component
public class StaticServicesInjector {

    private GameProgressValidator gameProgressValidator;
    private TeamService teamService;
    private GameWordService gameWordService;
    private WordService wordService;
    private GameService gameService;

    public StaticServicesInjector(GameProgressValidator gameProgressValidator, TeamService teamService,
                                  GameWordService gameWordService, WordService wordService, GameService gameService) {
        this.gameProgressValidator = gameProgressValidator;
        this.teamService = teamService;
        this.gameWordService = gameWordService;
        this.wordService = wordService;
        this.gameService = gameService;
    }

    @PostConstruct
    public void postConstruct() {
        GameProgress.setStaticDependencies(gameProgressValidator);
        Arrays.asList(GameProgress.values())
                .forEach(g -> {
                    if(g.equals(GameProgress.FIRST_ROUND)) {
                        g.setProgressProcessor(
                                new FirstRoundProcessor(teamService, gameWordService, wordService, gameService));
                    } else {
                        g.setProgressProcessor(DEFAULT_PROCESSOR);
                    }
                });
    }

    public static final Consumer<Game> DEFAULT_PROCESSOR = game -> {};

}

