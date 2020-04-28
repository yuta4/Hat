package com.yuta4.hat.controllers;

import com.yuta4.hat.GameProgress;
import com.yuta4.hat.components.GameProgressPublisher;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.services.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/progress")
@Slf4j
public class GameProgressController {

    private static final Logger logger = LoggerFactory.getLogger(GameProgressController.class);

    private final GameService gameService;
    private final PlayerService playerService;
    private final TeamService teamService;

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

    @PutMapping
    public Map<String, Object> getGameProgress(Principal principal, @RequestParam Long gameId) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        GameProgress gameProgress = game.getGameProgress();
        if(gameProgress.equals(GameProgress.TEAMS_FORMATION)) {
            gameService.addWatcher(game, player, teamService.getGamePlayers(game));
        }
        return gameProgress.getData(game);
    }

    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map<String, Object>>> getGameProgressEvents() {
        logger.error("getGameProgressEvents");
        return gameProgressFlux.log();
    }

    @PutMapping("move")
    public Map<String, Object> moveGameProgress(Principal principal, @RequestParam Long gameId,
                                                @RequestParam String progressToMoveTo) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        gameService.changeGameProgress(player, game, GameProgress.getByDisplayName(progressToMoveTo));
        return game.getGameProgress().getData(game);
    }
}
