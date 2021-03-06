package com.yuta4.hat.components.screen.processors;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.GameWord;
import com.yuta4.hat.entities.Word;
import com.yuta4.hat.services.GameService;
import com.yuta4.hat.services.GameWordService;
import com.yuta4.hat.services.TeamService;
import com.yuta4.hat.services.WordService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Consumer;

@Component
@Slf4j
public class FirstRoundProcessor implements Consumer<Game> {

    private static final Logger logger = LoggerFactory.getLogger(FirstRoundProcessor.class);

    private final TeamService teamService;
    private final GameWordService gameWordService;
    private final WordService wordService;
    private GameService gameService;

    public FirstRoundProcessor(TeamService teamService, GameWordService gameWordService,
                               WordService wordService, GameService gameService) {
        this.teamService = teamService;
        this.gameWordService = gameWordService;
        this.wordService = wordService;
        this.gameService = gameService;
    }

    @Override
    public void accept(Game game) {
        generateWordsIfNeeded(game);
        generateTurns(game);
    }

    private void generateTurns(Game game) {
        if(game.getTeamTurn() == null) {
            gameService.moveTeamTurn(game);
        }
        game.getTeams().stream()
                .filter(team -> team.getPlayerTurn() == null)
                .forEach(teamService::movePlayerTurn);
    }

    private void generateWordsIfNeeded(Game game) {
        int wordsRequired = getWordsRequired(game);
        if (checkIfWordsGeneratedRelevant(game, wordsRequired)) {
            logger.debug("Words for {} already generated", game);
            return;
        }
        Set<Word> generatedWords = wordService.generateRandomWordsByLevels(wordsRequired,
                game.getWordsLanguages(), game.getWordsLevels());
        gameWordService.convertFromWordsAndPersist(game, generatedWords);
    }

    private boolean checkIfWordsGeneratedRelevant(Game game, int wordsRequired) {
        if(game.getWords().size() != wordsRequired) {
            return false;
        }
        return game.getWords().stream()
                .map(GameWord::getWord)
                .anyMatch(w -> !game.getWordsLevels().contains(w.getLevel()) ||
                        !game.getWordsLanguages().contains(w.getLanguage()));
    }

    //TODO: overflow?
    private int getWordsRequired(Game game) {
        return teamService.getGamePlayers(game).size() * game.getWordsPerPlayer();
    }

}
