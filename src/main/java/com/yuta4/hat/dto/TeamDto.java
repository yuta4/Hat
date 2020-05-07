package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@AllArgsConstructor
@ToString
public class TeamDto {
    private Long id;
    private String name;
    private Set<String> players;
    private Long score;
}
