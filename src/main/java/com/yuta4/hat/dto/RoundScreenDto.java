package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@AllArgsConstructor
@ToString
public class RoundScreenDto implements ScreenDto {

    private final String owner;
    private final boolean allowSkipWords;
    private final Set<TeamDto> teams;
    private final Long teamTurn;
    private final String playerTurn;
    private final Integer round;
    private final String turnStatus;
    private final Long turnGuessesCount;
    private final Long turnSecondsRemaining;

}
