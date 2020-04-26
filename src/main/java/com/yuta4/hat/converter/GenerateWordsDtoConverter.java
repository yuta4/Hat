package com.yuta4.hat.converter;

import com.yuta4.hat.Language;
import com.yuta4.hat.Level;
import com.yuta4.hat.dto.GenerateWordsScreenDto;
import com.yuta4.hat.dto.StringCheckboxDto;
import com.yuta4.hat.entities.Game;
import org.springframework.core.convert.converter.Converter;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateWordsDtoConverter implements Converter<Game, GenerateWordsScreenDto> {

    @Override
    public GenerateWordsScreenDto convert(Game game) {
        return new GenerateWordsScreenDto(
                game.getOwner().getLogin(),
                Stream.of(Level.values())
                        .map(level -> new StringCheckboxDto(
                                level.getDisplayName(),
                                game.getWordsLevels().contains(level)))
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                Stream.of(Language.values())
                        .map(language -> new StringCheckboxDto(
                                language.getDisplayName(),
                                game.getWordsLanguages().contains(language)))
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                game.getWordsPerPlayer()
        );
    }

}
