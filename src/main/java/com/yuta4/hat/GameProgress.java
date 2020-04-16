package com.yuta4.hat;

import com.yuta4.hat.converter.TeamsScreenDtoConverter;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.services.GameService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;

public enum GameProgress {
//    CREATE_GAME("/create"),
    TEAMS_FORMATION("/teams/"),
    GENERATING_WORDS("/words/"),
    FIRST_ROUND("/first/"),
    SECOND_ROUND("/second/"),
    THIRD_ROUND("/third/"),
    SUMMERY_VIEW("/summary/");

    private final String path;
    private static GameService gameService;
    private static TeamsScreenDtoConverter teamsScreenDtoConverter;

    public static void setTeamService(GameService gameService, TeamsScreenDtoConverter teamsScreenDtoConverter) {
        GameProgress.gameService = gameService;
        GameProgress.teamsScreenDtoConverter = teamsScreenDtoConverter;
    }

    public Map<String, Object> getData(Game game) {
        if(this == TEAMS_FORMATION) {
            return Map.of(
                    "path", path,
                    "data", teamsScreenDtoConverter.convert(game)
            );
        }
        return Collections.emptyMap();
    }

    GameProgress(String path) {
        this.path = path;
    }

    @Component
    public static class GameProgressServiceInjector {

        private GameService gameService;
        private TeamsScreenDtoConverter teamsScreenDtoConverter;

        public GameProgressServiceInjector(GameService gameService, TeamsScreenDtoConverter teamsScreenDtoConverter) {
            this.gameService = gameService;
            this.teamsScreenDtoConverter = teamsScreenDtoConverter;
        }

        @PostConstruct
        public void postConstruct() {
            GameProgress.setTeamService(gameService, teamsScreenDtoConverter);
        }
    }

//    private static GameProgress[] vals = values();
//
//    public GameProgress next()
//    {
//        return vals[(this.ordinal() + 1) % vals.length];
//    }
//
//    public GameProgress previous()
//    {
//        return vals[(this.ordinal() - 1) % vals.length];
//    }
}
