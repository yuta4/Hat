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
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class GameWordService {

    private GameRepository gameRepository;
    private GameWordRepository gameWordRepository;
    private WordRepository wordRepository;
    private Random random;

    public GameWordService(GameRepository gameRepository, GameWordRepository gameWordRepository, WordRepository wordRepository) {
        this.gameRepository = gameRepository;
        this.gameWordRepository = gameWordRepository;
        this.wordRepository = wordRepository;
        random = new Random();
    }

    public void convertFromWordsAndPersist(Game game, List<Word> words) {
        List<GameWord> gameWords = words.stream()
                .map(word -> {
                    GameWord gameWord = new GameWord();
                    gameWord.setWord(word);
                    gameWord.setGame(game);
                    gameWordRepository.save(gameWord);
                    return gameWord;
                })
                .collect(Collectors.toList());
        game.setWords(gameWords);
        gameRepository.save(game);
    }

    public List<String> getNotGuessedWords(Game game) {
        return game.getWords().stream()
                .filter(gameWord -> gameWord.getTeam() == null)
                .map(gameWord -> gameWord.getWord().getString())
                .collect(Collectors.toList());
    }

    public void markAsGuessed(List<String> strings, Team team) {
        strings.stream()
                .map(str -> gameWordRepository.findByWord(
                        wordRepository.findByString(str)
                                .orElseThrow(() -> new WordException("Can't find word by string " + str)))
                        .orElseThrow(() -> new WordException(String.format("Can't find word %s in this game", str))))
                .forEach(gameWord -> {
                    gameWord.setTeam(team);
                    gameWordRepository.save(gameWord);
                });
    }
}
