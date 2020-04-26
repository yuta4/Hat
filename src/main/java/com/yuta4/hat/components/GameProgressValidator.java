package com.yuta4.hat.components;

import com.yuta4.hat.GameProgress;
import com.yuta4.hat.Language;
import com.yuta4.hat.Level;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.GameWord;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.services.TeamService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class GameProgressValidator {

    private TeamService teamService;

    public GameProgressValidator(TeamService teamService) {
        this.teamService = teamService;
    }

    public String validateRequirements(Game game) {
        Optional<String> errorOptional = Optional.empty();
        GameProgress requestedProgress = game.getGameProgress();
        switch (requestedProgress) {
            case TEAMS_FORMATION:
                errorOptional = checkTeamsFormation(game);
                break;
            case GENERATING_WORDS:
                errorOptional = checkGeneratingWords(game);
                break;
            case FIRST_ROUND:
                errorOptional = checkFirstRound(game);
                break;
            case SECOND_ROUND:
                errorOptional = checkSecondRound();
                break;
            case THIRD_ROUND:
                errorOptional = checkThirdRound();
                break;
            case SUMMERY_VIEW:
                errorOptional = checkSummaryView();
                break;
            default:
                break;
        }
        return errorOptional.orElse("");
    }

    private Optional<String> checkTeamsFormation(Game game) {
        List<Team> gameTeams = teamService.getGameTeams(game);
        if(gameTeams.size() < 2) {
            return Optional.of("At least 2 teams required to generate words for game " + game.getId());
        }
        return gameTeams.stream()
                .filter(team -> team.getPlayers().size() < 2)
                .map(team -> "At least 2 players need to be present in each team : " + team.getId())
                .findFirst();
    }

    private Optional<String> checkGeneratingWords(Game game) {
        Integer wordsPerPlayer = game.getWordsPerPlayer();
        if(wordsPerPlayer == null || wordsPerPlayer < 1) {
            return Optional.of("You need to have at least one word per player");
        }
        Set<Language> wordsLanguages = game.getWordsLanguages();
        if(wordsLanguages == null || wordsLanguages.isEmpty()) {
            return Optional.of("At least one language should be selected");
        }
        Set<Level> wordsLevels = game.getWordsLevels();
        if(wordsLevels == null || wordsLevels.isEmpty()) {
            return Optional.of("At least one words level should be selected");
        }
        return Optional.empty();
    }

    private Optional<String> checkFirstRound(Game game) {
        return Optional.empty();
    }

    private Optional<String> checkSecondRound() {
        return Optional.empty();
    }

    private Optional<String> checkThirdRound() {
        return Optional.empty();
    }

    private Optional<String> checkSummaryView() {
        return Optional.empty();
    }

}
