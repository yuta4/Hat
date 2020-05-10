package com.yuta4.hat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StartScreenDto {

    private final Long lastGameId;
    private final boolean isActive;

    @JsonProperty(value="isActive")
    public boolean isActive() {
        return isActive;
    }

}
