package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@AllArgsConstructor
@ToString
public class GenerateWordsScreenDto implements ScreenDto {

    private final String owner;
    private final Set<StringCheckboxDto> wordsLevels;
    private final Set<StringCheckboxDto> wordsLanguages;
    private final Integer wordsPerPlayer;
    private final Integer gameWords;
    private final boolean allowSkipWords;

}
