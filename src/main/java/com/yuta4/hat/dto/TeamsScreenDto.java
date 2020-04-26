package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@AllArgsConstructor
@ToString
public class TeamsScreenDto implements ScreenDto {
    private Set<TeamDto> teams;
    private String owner;
    private Set<String> watchers;
}
