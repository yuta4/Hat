package com.yuta4.hat.repositories;

import com.yuta4.hat.entities.Player;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface PlayerRepository extends PagingAndSortingRepository<Player, Long> {
    Optional<Player> findByLogin(String login);
}
