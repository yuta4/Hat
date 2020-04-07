package com.yuta4.hat.controllers;

import com.yuta4.hat.GameProgress;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.exceptions.GameProgressException;
import com.yuta4.hat.exceptions.NoSuchGameException;
import com.yuta4.hat.services.GameProgressService;
import com.yuta4.hat.services.GameService;
import com.yuta4.hat.services.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

@RestController
@RequestMapping("/progress")
public class GameProgressController {

    private GameService gameService;
    private GameProgressService gameProgressService;
    private PlayerService playerService;

    public GameProgressController(GameService gameService, GameProgressService gameProgressService, PlayerService playerService) {
        this.gameService = gameService;
        this.gameProgressService = gameProgressService;
        this.playerService = playerService;
    }

    @PutMapping("/next")
    public ResponseEntity<String> nextProgress(Principal principal) {
        try {
            Player player = playerService.getPlayerByEmail(principal.getName());
            Game game = player.getLastGame();
            GameProgress next = game.getGameProgress().next();
            gameProgressService.validateAndSaveProgress(game, next);
            return ResponseEntity.ok(next.toString());
        } catch (GameProgressException | NoSuchGameException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/previous")
    public ResponseEntity<String> previousProgress(Principal principal) {
        try {
            Player player = playerService.getPlayerByEmail(principal.getName());
            Game game = player.getLastGame();
            GameProgress previous = game.getGameProgress().previous();
            gameService.saveGameProgress(game, previous);
            return ResponseEntity.ok(previous.toString());
        } catch (NoSuchGameException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<String> getGameProgress(@RequestParam Long gameId) {
        Game game = gameService.getGameById(gameId);
        return Collections.singleton(game.getGameProgress().getPath());
    }
}
