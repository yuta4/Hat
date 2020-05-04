package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class SummaryDto implements ScreenDto {

    private String owner;
    private Set<TeamDto> teams;

}
