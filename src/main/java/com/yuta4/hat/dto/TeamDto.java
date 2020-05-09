package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@AllArgsConstructor
@ToString
public class TeamDto {

    private final Long id;
    private final String name;
    private final Set<String> players;
    private final Long score;

}
