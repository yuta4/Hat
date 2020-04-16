package com.yuta4.hat.services;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.exceptions.RequestValidationException;
import org.springframework.stereotype.Service;

@Service
public class RequestValidationService {

    public void validate(Long gameId, Player player, Game game) {
        if(!game.getId().equals(gameId) || (player != null && !player.equals(game.getOwner()))) {
            throw new RequestValidationException(String.format("Check request owner and gameId failed: %s, %d, %d",
                    player, gameId, game.getId()));
        }
    }

    public void validateTeamReduction(Player requester, Player playerToBeRemoved, Team team) {
        if(!team.getPlayers().contains(playerToBeRemoved) ||
                (requester.equals(playerToBeRemoved) || requester.equals(team.getGame().getOwner()))) {
            throw new RequestValidationException(
                    String.format("Team reduction request failed: player to be removed %s, requester %s, team %d",
                    playerToBeRemoved, requester, team.getId()));
        }
    }

    public void validateTeamExtension(Player requester, Player playerToBeAdded, Team team) {
        if(team.getPlayers().contains(playerToBeAdded) ||
                !requester.equals(team.getGame().getOwner())) {
            throw new RequestValidationException(
                    String.format("Team extension request failed: player to be added %s, requester %s, team %d",
                            playerToBeAdded, requester, team.getId()));
        }
    }

    //validation without checking gameOwner
    public void validate(Long gameId, Game game) {
        validate(gameId, null, game);
    }

}
