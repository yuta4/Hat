package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class TeamsScreenDto {
    private Set<TeamDto> teams;
    private String owner;
    private Set<String> watchers;
}
