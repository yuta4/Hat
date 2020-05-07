package com.yuta4.hat.controllers;

import com.yuta4.hat.components.JoinGamePublisher;
import com.yuta4.hat.dto.JoinGameDto;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.services.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/game")
@Slf4j
public class GameController {

    private final GameService gameService;
    private final PlayerService playerService;
    private final WordService wordService;
    private final TeamService teamService;
    private final GameWordService gameWordService;
    private final Flux<ServerSentEvent<Set<JoinGameDto>>> joinGameFlux;
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    public GameController(GameService gameService, PlayerService playerService, WordService wordService,
                          TeamService teamService, GameWordService gameWordService, JoinGamePublisher joinGamePublisher) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.wordService = wordService;
        this.teamService = teamService;
        this.gameWordService = gameWordService;
        this.joinGameFlux = Flux.create(joinGamePublisher).share();
    }

    @GetMapping("login")
    public Map.Entry<String, String> getLogin(Principal principal) {
        return new AbstractMap.SimpleEntry<>(
                        "login", playerService.getPlayerByLogin(principal.getName()).getLogin());
    }

    @PostMapping("/create")
    public ResponseEntity<Long> startGame(Principal principal) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.createGame(player);
//        gameService.closeAllGamesOwnedBy();
        playerService.setLastGame(player, game);
        return ResponseEntity.ok().body(game.getId());
    }

    @GetMapping
    public ResponseEntity<Long> getActiveGame(Principal principal) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = player.getLastGame();
        if(game != null && !Boolean.FALSE.equals(game.getIsActive()) &&
                (game.getOwner().equals(player) || teamService.getGamePlayers(game).contains(player))) {
            return ResponseEntity.ok().body(game.getId());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("changeWatcher")
    public ResponseEntity<Boolean> removeWatcher(Principal principal, @RequestParam Long gameId,
                                                 @RequestParam boolean value) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        if(value) {
            Game game = gameService.getGameById(gameId);
            gameService.addWatcher(game, player, teamService.getGamePlayers(game));
        } else {
            gameService.removeWatcher(gameId, player);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/finish")
    public ResponseEntity<Void> finishGame(Principal principal, @RequestParam Long gameId) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        gameService.finishGameIfPermitted(game, player);
        return ResponseEntity.ok().build();
    }

    @GetMapping("notStarted")
    public Set<JoinGameDto> getNotStartedGames() {
        return gameService.getNotStartedGames().stream()
                .map(JoinGameDto::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @GetMapping(path = "notStarted/events/{player}",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Set<JoinGameDto>>> notStartedEvents(@PathVariable(required = false) String player) {
        logger.info("notStartedEvents {}", player);
        return joinGameFlux
                .log();
    }

}
