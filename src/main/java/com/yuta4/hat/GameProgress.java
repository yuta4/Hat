package com.yuta4.hat;

import com.yuta4.hat.components.GameProgressValidator;
import com.yuta4.hat.converter.TeamsScreenDtoConverter;
import com.yuta4.hat.entities.Game;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public enum GameProgress {
    TEAMS_FORMATION("Teams formation","/teams/"),
    GENERATING_WORDS("Generating words","/words/"),
    FIRST_ROUND("First round","/first/"),
    SECOND_ROUND("Second round","/second/"),
    THIRD_ROUND("Third round","/third/"),
    SUMMERY_VIEW("Summary","/summary/");

    private final String path;
    private static TeamsScreenDtoConverter teamsScreenDtoConverter;
    private static GameProgressValidator gameProgressValidator;
    private final String displayName;

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

    GameProgress(String displayName, String path) {
        this.displayName = displayName;
        this.path = path;
    }

    public static GameProgress getByDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(gp -> gp.displayName.equals(displayName))
                .findFirst()
                .orElseThrow();
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
