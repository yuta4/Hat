package com.yuta4.hat.repositories;

import com.yuta4.hat.Language;
import com.yuta4.hat.Level;
import com.yuta4.hat.entities.Word;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WordRepository extends CrudRepository<Word, Long> {

    Optional<Word> findByString(String string);

    List<Word> findByLanguageInAndLevelInOrderByUsed(Set<Language > includeLanguages, Set<Level> includeLevels, Pageable pageable);

    List<Word> findByLanguageInAndLevelNotInOrderByUsed(Set<Language > includeLanguages, Set<Level> excludeLevels, Pageable pageable);

}
