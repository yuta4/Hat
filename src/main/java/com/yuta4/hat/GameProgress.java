package com.yuta4.hat;

import com.yuta4.hat.components.GameProgressValidator;
import com.yuta4.hat.converters.GenerateWordsDtoConverter;
import com.yuta4.hat.converters.RoundScreenDtoConverter;
import com.yuta4.hat.converters.SummaryScreenDtoConverter;
import com.yuta4.hat.converters.TeamsScreenDtoConverter;
import com.yuta4.hat.dto.ScreenDto;
import com.yuta4.hat.entities.Game;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

public enum GameProgress {
    TEAMS_FORMATION("Teams formation", "/teams/",
            new TeamsScreenDtoConverter()),
    GENERATING_WORDS("Generating words", "/words/",
            new GenerateWordsDtoConverter()),
    FIRST_ROUND("Round 1", "/round/1/",
            new RoundScreenDtoConverter(1)),
    SECOND_ROUND("Round 2", "/round/2/",
            new RoundScreenDtoConverter(2)),
    THIRD_ROUND("Round 3", "/round/3/",
            new RoundScreenDtoConverter(3)),
    SUMMERY_VIEW("Summary", "/summary/",
            new SummaryScreenDtoConverter());

    private final String path;
    private static GameProgressValidator gameProgressValidator;
    private final String displayName;
    private final Converter<Game, ? extends ScreenDto> dataConverter;
    private Consumer<Game> progressProcessor;

    public static void setStaticDependencies(GameProgressValidator gameProgressValidator) {
        GameProgress.gameProgressValidator = gameProgressValidator;
    }

    public Map<String, Object> getData(Game game) {
        return Map.of(
                "path", path + game.getId(),
                "data", dataConverter.convert(game),
                "validation", gameProgressValidator.validateRequirements(game));
    }

    public void proceedGameProgress(Game game) {
        progressProcessor.accept(game);
    }

    GameProgress(String displayName, String path,
                 Converter<Game, ? extends ScreenDto> dataConverter) {
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

    public GameProgress getNext() {
        return this.ordinal() < GameProgress.values().length - 1
                ? GameProgress.values()[this.ordinal() + 1]
                : null;
    }

    public void setProgressProcessor(Consumer<Game> progressProcessor) {
        this.progressProcessor = progressProcessor;
    }
}
