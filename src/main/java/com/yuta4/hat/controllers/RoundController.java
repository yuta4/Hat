package com.yuta4.hat.controllers;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.exceptions.RequestValidationException;
import com.yuta4.hat.services.GameService;
import com.yuta4.hat.services.GameWordService;
import com.yuta4.hat.services.PlayerService;
import com.yuta4.hat.services.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

@Controller
@Slf4j
@RequestMapping("/round")
public class RoundController {


    private PlayerService playerService;
    private GameWordService gameWordService;
    private TeamService teamService;
    private GameService gameService;

    public RoundController(PlayerService playerService, GameWordService gameWordService, TeamService teamService, GameService gameService) {
        this.playerService = playerService;
        this.gameWordService = gameWordService;
        this.teamService = teamService;
        this.gameService = gameService;
    }

    @PutMapping("/start")
    public Set<String> startRound(Principal principal, @RequestParam Long gameId, @RequestParam(defaultValue = "true") boolean wordsRequested,
                                                  @RequestParam(defaultValue = "false") boolean wasPaused) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        validatePlayer(player, game);
        if(wasPaused) {
            gameService.unPauseTurn(gameId);
        } else {
            gameService.startTurn(gameId);
        }
        return wordsRequested ? gameWordService.getNotGuessedWords(game) : Collections.EMPTY_SET;
    }

    private void validatePlayer(Player player, Game game) {
        if(game.getTeamTurn() == null) {
            throw new RequestValidationException("No team turn set up");
        }
        if(!player.equals(game.getTeamTurn().getPlayerTurn())) {
            throw new RequestValidationException("Wrong player turn " + player);
        }
    }

    @PutMapping("/pause")
    public ResponseEntity<Void> pauseRound(Principal principal, @RequestParam Long gameId) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        validatePlayer(player, game);
        gameService.pauseTurn(gameId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/finish")
    public ResponseEntity<Void> finishRound(Principal principal, @RequestParam Long gameId, @RequestParam Set<String> guessedWords) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        validatePlayer(player, game);
        teamService.movePlayerTurn(game.getTeamTurn());
        gameService.finishTurn(gameId, guessedWords, teamService.getPlayerTeam(player));
        return ResponseEntity.ok().build();
    }

}
