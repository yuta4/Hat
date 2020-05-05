package com.yuta4.hat.controllers;

import com.yuta4.hat.dto.TurnWordDto;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.GameWord;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.exceptions.RequestValidationException;
import com.yuta4.hat.services.GameService;
import com.yuta4.hat.services.GameWordService;
import com.yuta4.hat.services.PlayerService;
import com.yuta4.hat.services.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/turn")
public class TurnController {

    private PlayerService playerService;
    private GameWordService gameWordService;
    private TeamService teamService;
    private GameService gameService;
    private Converter<List<GameWord>, Set<TurnWordDto>> turnWordsDtoConverter;

    public TurnController(PlayerService playerService, GameWordService gameWordService, TeamService teamService,
                          GameService gameService, Converter<List<GameWord>, Set<TurnWordDto>> turnWordsDtoConverter) {
        this.playerService = playerService;
        this.gameWordService = gameWordService;
        this.teamService = teamService;
        this.gameService = gameService;
        this.turnWordsDtoConverter = turnWordsDtoConverter;
    }

    @PutMapping("/start")
    public Set<String> startTurn(Principal principal, @RequestParam Long gameId,
                                 @RequestParam(defaultValue = "true") boolean wordsRequested,
                                 @RequestParam(defaultValue = "false") boolean wasPaused) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        validatePlayer(player, game);
        if (wasPaused) {
            gameService.unPauseTurn(gameId);
        } else {
            gameService.startTurn(gameId);
        }
        return wordsRequested ? gameWordService.getNotGuessedWords(game) : Collections.EMPTY_SET;
    }

    private void validatePlayer(Player player, Game game) {
        if (game.getTeamTurn() == null) {
            throw new RequestValidationException("No team turn set up");
        }
        if (!player.equals(game.getTeamTurn().getPlayerTurn())) {
            throw new RequestValidationException("Wrong player turn " + player + ", " + game.getTeamTurn().getPlayerTurn());
        }
    }

    private void validatePlayer(Player player, Game game, Team team) {
        validatePlayer(player, game);
        if (!team.getGame().equals(game) || !team.getPlayers().contains(player)) {
            throw new RequestValidationException(
                    String.format("Wrong team request : %s, %s", team, game));
        }
    }

    @PutMapping("/pause")
    public ResponseEntity<Void> pauseTurn(Principal principal, @RequestParam Long gameId) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        validatePlayer(player, game);
        gameService.pauseTurn(gameId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark")
    public ResponseEntity<Object> markTurnWord(Principal principal, @RequestParam Long gameId, @RequestParam Long teamId,
                                               @RequestParam String word, @RequestParam boolean isGuessed) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        Team team = teamService.getTeamOrThrow(teamId);
        validatePlayer(player, game, team);
        if (gameWordService.markTurnWordAndCheckIfRoundCompleted(game, team, word, isGuessed)) {
            gameService.finishTurn(gameId);
            return ResponseEntity.ok(
                    gameWordService.getCurrentTurnWords(gameId));
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/finish")
    public ResponseEntity<Set<TurnWordDto>> finishTurn(Principal principal, @RequestParam Long gameId) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        validatePlayer(player, game);
        gameService.finishTurn(gameId);
        return ResponseEntity.ok(
                Objects.requireNonNull(
                        turnWordsDtoConverter.convert(
                                gameWordService.getCurrentTurnWords(gameId))));
    }

    @PutMapping("/approve")
    public ResponseEntity<Void> approveTurn(Principal principal, @RequestParam Long gameId,
                                            @RequestParam Long teamId, @RequestParam Set<String> guessedWords) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        Team team = teamService.getTeamOrThrow(teamId);
        validatePlayer(player, game, team);
        teamService.movePlayerTurn(game.getTeamTurn());
        gameService.approveTurn(gameId, guessedWords, team);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/words/approvement")
    public ResponseEntity<Set<TurnWordDto>> getCurrentTurnWords(Principal principal, @RequestParam Long gameId) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        validatePlayer(player, game);
        return ResponseEntity.ok(
                Objects.requireNonNull(
                        turnWordsDtoConverter.convert(
                                gameWordService.getCurrentTurnWords(gameId))));
    }

}
