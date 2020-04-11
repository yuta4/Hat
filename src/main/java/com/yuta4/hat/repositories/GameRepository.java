package com.yuta4.hat.repositories;

import com.yuta4.hat.entities.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface GameRepository extends CrudRepository<Game, Long> {
    Set<Game> findGamesByIsActiveIsNull();
}
