package com.yuta4.hat.repositories;

import com.yuta4.hat.entities.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlayerRepository extends CrudRepository<Player, Long> {
    Optional<Player> findByEmail(String email);
}
