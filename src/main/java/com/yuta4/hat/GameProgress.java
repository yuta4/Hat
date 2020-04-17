package com.yuta4.hat;

import com.yuta4.hat.components.GameProgressValidator;
import com.yuta4.hat.converter.TeamsScreenDtoConverter;
import com.yuta4.hat.entities.Game;

import java.util.Collections;
import java.util.Map;

public enum GameProgress {
    TEAMS_FORMATION("/teams/"),
    GENERATING_WORDS("/words/"),
    FIRST_ROUND("/first/"),
    SECOND_ROUND("/second/"),
    THIRD_ROUND("/third/"),
    SUMMERY_VIEW("/summary/");

    private final String path;
    private static TeamsScreenDtoConverter teamsScreenDtoConverter;
    private static GameProgressValidator gameProgressValidator;

    public static void setTeamService(TeamsScreenDtoConverter teamsScreenDtoConverter, GameProgressValidator gameProgressValidator) {
        GameProgress.teamsScreenDtoConverter = teamsScreenDtoConverter;
        GameProgress.gameProgressValidator = gameProgressValidator;
    }

    public Map<String, Object> getData(Game game) {
        if(this == TEAMS_FORMATION) {
            return Map.of(
                    "path", path + game.getId(),
                    "data", teamsScreenDtoConverter.convert(game),
                    "validation", gameProgressValidator.validateRequirements(game)
            );
        }
        return Collections.emptyMap();
    }

    GameProgress(String path) {
        this.path = path;
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
