package com.yuta4.hat;

public enum GameProgress {
    CREATE_GAME("/create"),
    TEAMS_FORMATION("/teams/"),
    GENERATING_WORDS("/words/"),
    FIRST_ROUND("/first/"),
    SECOND_ROUND("/second/"),
    THIRD_ROUND("/third/"),
    SUMMERY_VIEW("/summary/");

    private final String path;

    private GameProgress(String path) {
        this.path = path;
    }

    private static GameProgress[] vals = values();

    public GameProgress next()
    {
        return vals[(this.ordinal() + 1) % vals.length];
    }

    public GameProgress previous()
    {
        return vals[(this.ordinal() - 1) % vals.length];
    }

    public String getPath() {
        return path;
    }
}
