package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@AllArgsConstructor
@ToString
public class GenerateWordsScreenDto implements ScreenDto {

    private String owner;
    private Set<StringCheckboxDto> wordsLevels;
    private Set<StringCheckboxDto> wordsLanguages;
    private Integer wordsPerPlayer;

}
