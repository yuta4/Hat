package com.yuta4.hat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TurnWordDto {
    private String word;
    private boolean isGuessed;
}
