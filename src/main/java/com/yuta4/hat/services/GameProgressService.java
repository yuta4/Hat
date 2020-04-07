package com.yuta4.hat.services;

import com.yuta4.hat.GameProgress;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.GameWord;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.exceptions.GameProgressException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameProgressService {

    private TeamService teamService;
    private GameService gameService;

    public GameProgressService(TeamService teamService, GameService gameService) {
        this.teamService = teamService;
        this.gameService = gameService;
    }

    public void validateAndSaveProgress(Game game, GameProgress requestedProgress) {
        Optional<String> errorOptional = Optional.empty();
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
        errorOptional.ifPresent(error -> {
            throw new GameProgressException(requestedProgress, error);
        } );
        gameService.saveGameProgress(game, requestedProgress);
    }

    private Optional<String> checkTeamsFormation(Game game) {
        if(game == null) {
            return Optional.of("Game wasn't created");
        }
        return Optional.empty();
    }

    private Optional<String> checkGeneratingWords(Game game) {
        List<Team> gameTeams = teamService.getGameTeams(game);
        if(gameTeams.size() < 2) {
            return Optional.of("At least 2 teams required to generate words for game " + game.getId());
        }
        return Optional.empty();
    }

    private Optional<String> checkFirstRound(Game game) {
        List<GameWord> gameWords = game.getWords();
        if(gameWords == null || gameWords.isEmpty()) {
            return Optional.of("There are no words generated for this game");
        }
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
