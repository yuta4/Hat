package com.yuta4.hat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class TurnWordDto {
    private String word;

    private boolean isGuessed;

    @JsonProperty(value="isGuessed")
    public boolean getGuessed() {
        return isGuessed;
    }
}
