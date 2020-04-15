package com.yuta4.hat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DataDto {
    private Map<String, String> data;
}
