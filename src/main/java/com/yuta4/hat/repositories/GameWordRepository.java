package com.yuta4.hat.repositories;

import com.yuta4.hat.entities.GameWord;
import com.yuta4.hat.entities.Word;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GameWordRepository extends CrudRepository<GameWord, Long> {

    Optional<GameWord> findByWord(Word word);

}
