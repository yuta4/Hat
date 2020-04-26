package com.yuta4.hat;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum Level {
    EASY("Easy"),
    MIDDLE("Middle"),
    HARD("Hard"),
    UNKNOWN("Unknown");

    private String displayName;

    Level(String displayName) {
        this.displayName = displayName;
    }

    public static Level getByDisplayName(String displayName) {
        return Stream.of(values())
                .filter(l -> l.getDisplayName().equals(displayName))
                .findFirst()
                .orElseThrow();
    }
}
