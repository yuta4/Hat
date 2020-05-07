package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class RoundScreenDto implements ScreenDto {

    private String owner;
    private boolean allowSkipWords;
    private Set<TeamDto> teams;
    private Long teamTurn;
    private String playerTurn;
    private Integer round;
    private String turnStatus;
    private Long turnGuessesCount;
    private Long turnSecondsRemaining;

}
