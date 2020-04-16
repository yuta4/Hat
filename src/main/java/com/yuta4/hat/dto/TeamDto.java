package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class TeamDto {
    private Long id;
    private String name;
    private Set<String> players;
}
