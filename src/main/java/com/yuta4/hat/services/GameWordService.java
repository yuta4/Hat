package com.yuta4.hat.services;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.GameWord;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.entities.Word;
import com.yuta4.hat.exceptions.WordException;
import com.yuta4.hat.repositories.GameRepository;
import com.yuta4.hat.repositories.GameWordRepository;
import com.yuta4.hat.repositories.WordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameWordService {

    private GameRepository gameRepository;
    private GameWordRepository gameWordRepository;
    private WordRepository wordRepository;

    public GameWordService(GameRepository gameRepository, GameWordRepository gameWordRepository,
                           WordRepository wordRepository) {
        this.gameRepository = gameRepository;
        this.gameWordRepository = gameWordRepository;
        this.wordRepository = wordRepository;
    }

    public void convertFromWordsAndPersist(Game game, Set<Word> words) {
        Set<GameWord> gameWords = words.stream()
                .map(word -> {
                    GameWord gameWord = new GameWord();
                    gameWord.setWord(word);
                    gameWord.setGame(game);
                    return gameWord;
                })
                .collect(Collectors.toSet());
        game.setWords(gameWords);
        gameRepository.save(game);
    }

    public Set<String> getNotGuessedWords(Game game) {
        return game.getWords().stream()
                .filter(gameWord -> gameWord.getTeam() == null)
                .map(gameWord -> gameWord.getWord().getString())
                .collect(Collectors.toSet());
    }

    public void markAsGuessed(Game game, Set<String> strings, Team team) {
        gameWordRepository.clearCurrentTurnWords(team.getGame());
        strings.stream()
                .map(str -> gameWordRepository.findByGameAndWord(game,
                        wordRepository.findByString(str)
                                .orElseThrow(() -> new WordException("Can't find word by string " + str)))
                        .orElseThrow(() -> new WordException(String.format("Can't find word %s in this game", str))))
                .forEach(gameWord -> {
                    gameWord.setTeam(team);
//                    gameWordRepository.save(gameWord);
                });
    }

    public boolean markTurnWordAndCheckIfRoundCompleted(Game game, Team team, String word, boolean isGuessed) {
        GameWord gameWord = gameWordRepository.findByGameAndWord(game,
                wordRepository.findByString(word)
                        .orElseThrow(() -> new WordException("Can't find word by string " + word)))
                .orElseThrow(() -> new WordException(String.format("Can't find word %s in this game", word)));
        gameWord.setCurrentTurnGuessed(isGuessed);
        gameWord.setTeam(team);
        gameWordRepository.save(gameWord);
        return getNotGuessedWords(game).isEmpty();
    }

    public List<GameWord> getCurrentTurnWords(Long gameId) {
        return gameWordRepository.findByGameIdAndCurrentTurnGuessedIsNotNull(gameId);
    }
}
