package com.yuta4.hat;

public enum GameProgress {
    TEAMS_FORMATION,
    GENERATING_WORDS,
    FIRST_ROUND,
    SECOND_ROUND,
    THIRD_ROUND,
    SUMMERY_VIEW;

    private static GameProgress[] vals = values();

    public GameProgress next()
    {
        return vals[(this.ordinal() + 1) % vals.length];
    }

    public GameProgress previous()
    {
        return vals[(this.ordinal() - 1) % vals.length];
    }

}
