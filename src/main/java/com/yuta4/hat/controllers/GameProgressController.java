package com.yuta4.hat.controllers;

import com.yuta4.hat.components.GameProgressPublisher;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.services.*;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/progress")
public class GameProgressController {

    private GameService gameService;
    private PlayerService playerService;
    private TeamService teamService;

    private Flux<ServerSentEvent<Map<String, Object>>> gameProgressFlux;

    public GameProgressController(GameService gameService,
                                  PlayerService playerService,
                                  TeamService teamService,
                                  GameProgressPublisher gameProgressPublisher
    ) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.teamService = teamService;
        this.gameProgressFlux = Flux.create(gameProgressPublisher).share();
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

    @PutMapping
    public Map<String, Object> getGameProgress(Principal principal, @RequestParam Long gameId) {
        Player pLayer = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        gameService.addWatcher(game, pLayer, teamService.getGamePlayers(game));
        return game.getGameProgress().getData(game);
    }

    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map<String, Object>>> getGameProgressEvents() {
        return gameProgressFlux.log();
    }
}
