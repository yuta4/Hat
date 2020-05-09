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
import org.springframework.transaction.annotation.Transactional;

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

    public List<GameWord> getCurrentTurnWords(Long gameId) {
        return gameWordRepository.findByGameIdAndCurrentTurnGuessedIsNotNull(gameId);
    }
}
