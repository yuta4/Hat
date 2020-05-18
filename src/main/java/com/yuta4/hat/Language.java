package com.yuta4.hat;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum Language {
    UKRAINIAN("Ukrainian"),
    ENGLISH("English"),
    RUSSIAN("Russian");

    private String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public static Language getByDisplayName(String displayName) {
        return Stream.of(values())
                .filter(l -> l.getDisplayName().equals(displayName))
                .findFirst()
                .orElseThrow();
    }

}
