package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class RoundScreenDto implements ScreenDto {

    private String owner;
    private Set<TeamDto> teams;
    private String teamTurn;
    private String playerTurn;
    private Integer round;

}
