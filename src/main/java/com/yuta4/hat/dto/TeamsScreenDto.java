package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@AllArgsConstructor
@ToString
public class TeamsScreenDto implements ScreenDto {

    private final Set<TeamDto> teams;
    private final String owner;
    private final Set<String> watchers;

}
