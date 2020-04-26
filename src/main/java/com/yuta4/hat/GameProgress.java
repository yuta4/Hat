package com.yuta4.hat;

import com.yuta4.hat.components.GameProgressValidator;
import com.yuta4.hat.converter.GenerateWordsDtoConverter;
import com.yuta4.hat.converter.TeamsScreenDtoConverter;
import com.yuta4.hat.dto.ScreenDto;
import com.yuta4.hat.entities.Game;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;
import java.util.Map;

public enum GameProgress {
    TEAMS_FORMATION("Teams formation", "/teams/",
            new TeamsScreenDtoConverter()),
    GENERATING_WORDS("Generating words", "/words/",
            new GenerateWordsDtoConverter()),
    FIRST_ROUND("First round", "/first/", game -> null),
    SECOND_ROUND("Second round", "/second/", game -> null),
    THIRD_ROUND("Third round", "/third/", game -> null),
    SUMMERY_VIEW("Summary", "/summary/", game -> null);

    private final String path;
    private static GameProgressValidator gameProgressValidator;
    private final String displayName;
    private final Converter<Game, ? extends ScreenDto> dataConverter;

    public static void setTeamService(GameProgressValidator gameProgressValidator) {
        GameProgress.gameProgressValidator = gameProgressValidator;
    }

    public Map<String, Object> getData(Game game) {
        return Map.of(
                "path", path + game.getId(),
                "data", dataConverter.convert(game),
                "validation", gameProgressValidator.validateRequirements(game));
    }

    GameProgress(String displayName, String path, Converter<Game, ? extends ScreenDto> dataConverter) {
        this.displayName = displayName;
        this.path = path;
        this.dataConverter = dataConverter;
    }

    public static GameProgress getByDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(gp -> gp.displayName.equals(displayName))
                .findFirst()
                .orElseThrow();
    }
}
