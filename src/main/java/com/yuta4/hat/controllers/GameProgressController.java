package com.yuta4.hat.controllers;

import com.yuta4.hat.GameProgress;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.exceptionas.GameProgressException;
import com.yuta4.hat.exceptionas.NoSuchGameException;
import com.yuta4.hat.services.GameProgressService;
import com.yuta4.hat.services.GameService;
import com.yuta4.hat.services.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
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
}
