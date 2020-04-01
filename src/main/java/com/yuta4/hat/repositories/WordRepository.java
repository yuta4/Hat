package com.yuta4.hat.repositories;

import com.yuta4.hat.Level;
import com.yuta4.hat.entities.Word;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface WordRepository extends CrudRepository<Word, Long> {

    Optional<Word> findByString(String string);

    List<Word> findByLevelInOrderByUsed(List<Level> includeLevelList, Pageable pageable);

    List<Word> findByLevelNotInOrderByUsed(List<Level> excludeLevelList, Pageable pageable);

}
