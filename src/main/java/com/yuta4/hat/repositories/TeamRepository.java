package com.yuta4.hat.repositories;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Team;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepository extends CrudRepository<Team, Long> {

    List<Team> findTeamsByGame(Game game);

}
