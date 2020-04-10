package com.yuta4.hat.services;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.exceptions.RequestValidationException;
import org.springframework.stereotype.Service;

@Service
public class RequestValidationService {

    public void validate(Long gameID, Player player, Game game) {
        if(!game.getId().equals(gameID) || (player != null && !player.equals(game.getOwner()))) {
            throw new RequestValidationException(String.format("Check request owner and gameID failed: %s, %d, %d",
                    player, gameID, game.getId()));
        }
    }

    //validation without checking gameOwner
    public void validate(Long gameID, Game game) {
        validate(gameID, null, game);
    }

}
