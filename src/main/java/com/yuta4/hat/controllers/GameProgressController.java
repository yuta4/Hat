package com.yuta4.hat.controllers;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.services.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/progress")
public class GameProgressController {

    private GameService gameService;
    private GameProgressService gameProgressService;
    private PlayerService playerService;
    private RequestValidationService requestValidationService;
    private TeamService teamService;

    public GameProgressController(GameService gameService, GameProgressService gameProgressService,
                                  PlayerService playerService, RequestValidationService requestValidationService,
                                  TeamService teamService) {
        this.gameService = gameService;
        this.gameProgressService = gameProgressService;
        this.playerService = playerService;
        this.requestValidationService = requestValidationService;
        this.teamService = teamService;
    }

//    @PutMapping("/next")
//    public ResponseEntity<String> nextProgress(Principal principal) {
//        try {
//            Player player = playerService.getPlayerByEmail(principal.getName());
//            Game game = player.getLastGame();
//            GameProgress next = game.getGameProgress().next();
//            gameProgressService.validateAndSaveProgress(game, next);
//            return ResponseEntity.ok(next.toString());
//        } catch (GameProgressException | NoSuchGameException ex) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
//        }
//    }
//
//    @PutMapping("/previous")
//    public ResponseEntity<String> previousProgress(Principal principal) {
//        try {
//            Player player = playerService.getPlayerByEmail(principal.getName());
//            Game game = player.getLastGame();
//            GameProgress previous = game.getGameProgress().previous();
//            gameService.saveGameProgress(game, previous);
//            return ResponseEntity.ok(previous.toString());
//        } catch (NoSuchGameException ex) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
//        }
//    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getGameProgress(Principal principal, @RequestParam Long gameId) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        Set<Player> gamePlayers = teamService.getGamePlayers(game);
        Set<String> gamePlayersLogin = gamePlayers.stream()
                .map(Player::getLogin)
                .collect(Collectors.toSet());
        Set<String> watchersLogin = gameService.addAndGetWatchersLogin(gameId, player, gamePlayers);
        return Map.of("path", game.getGameProgress().getPath(gameId),
                "owner", game.getOwner().getLogin(),
                "watchers", watchersLogin,
                "players", gamePlayersLogin);
    }
}
