package com.yuta4.hat.repositories;

import com.yuta4.hat.entities.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long> {
}
