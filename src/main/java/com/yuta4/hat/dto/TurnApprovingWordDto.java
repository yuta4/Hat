package com.yuta4.hat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class TurnApprovingWordDto {

    private final String word;
    private final boolean isGuessed;

    @JsonProperty(value="isGuessed")
    public boolean getGuessed() {
        return isGuessed;
    }
}
