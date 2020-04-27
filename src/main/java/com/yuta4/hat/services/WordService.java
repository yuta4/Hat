package com.yuta4.hat.services;

import com.yuta4.hat.Language;
import com.yuta4.hat.Level;
import com.yuta4.hat.entities.Word;
import com.yuta4.hat.repositories.WordRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class WordService {

    private WordRepository wordRepository;

    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public Set<Word> generateRandomWordsByLevels(int count, Set<Language> languages, Set<Level> levels) {
        Set<Word> randomWords = new HashSet<>(wordRepository.findByLanguageInAndLevelInOrderByUsed(languages, levels,
                PageRequest.of(0, count)));
        if(randomWords.size() < count) {
            int wordsToAdd = count - randomWords.size();
            randomWords.addAll(wordRepository.findByLanguageInAndLevelNotInOrderByUsed(languages, levels,
                    PageRequest.of(0, wordsToAdd)));
        }
        randomWords.forEach(word -> {
            word.setUsed(word.getUsed() + 1);
            wordRepository.save(word);
        });
        return randomWords;
    }

}
