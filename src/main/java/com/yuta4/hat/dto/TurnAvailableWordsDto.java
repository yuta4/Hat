package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class TurnAvailableWordsDto {

    private final Set<String> words;
    private final String currentGuessing;

}
