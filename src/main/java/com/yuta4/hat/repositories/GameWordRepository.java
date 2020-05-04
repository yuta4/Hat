package com.yuta4.hat.repositories;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.GameWord;
import com.yuta4.hat.entities.Word;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameWordRepository extends CrudRepository<GameWord, Long> {

    Optional<GameWord> findByGameAndWord(Game game, Word word);

    @Modifying
    @Query("update GameWord gameWord set gameWord.currentTurnGuessed = null where gameWord.game =:game")
    void clearCurrentTurnWords(@Param("game") Game game);

    List<GameWord> findByGameIdAndCurrentTurnGuessedIsNotNull(Long gameId);

}
