package com.yuta4.hat.controllers;

import com.yuta4.hat.dto.TurnApprovingWordDto;
import com.yuta4.hat.dto.TurnAvailableWordsDto;
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
    private Converter<List<GameWord>, Set<TurnApprovingWordDto>> turnApprovingWordsDtoConverter;
    private Converter<Game, TurnAvailableWordsDto> turnAvailableWordsDtoConverter;

    public TurnController(PlayerService playerService, GameWordService gameWordService, TeamService teamService,
                          GameService gameService, Converter<List<GameWord>, Set<TurnApprovingWordDto>> turnApprovingWordsDtoConverter, Converter<Game, TurnAvailableWordsDto> turnAvailableWordsDtoConverter) {
        this.playerService = playerService;
        this.gameWordService = gameWordService;
        this.teamService = teamService;
        this.gameService = gameService;
        this.turnApprovingWordsDtoConverter = turnApprovingWordsDtoConverter;
        this.turnAvailableWordsDtoConverter = turnAvailableWordsDtoConverter;
    }

    @PutMapping("/start")
    public TurnAvailableWordsDto startTurn(Principal principal, @RequestParam Long gameId,
                                           @RequestParam(defaultValue = "false") boolean wasPaused) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        validatePlayer(player, game);
        if (wasPaused) {
            gameService.unPauseTurn(gameId);
        } else {
            gameService.startTurn(gameId);
        }
        return turnAvailableWordsDtoConverter.convert(game);
    }

    @GetMapping("/words")
    public TurnAvailableWordsDto getAvailableWords(Principal principal, @RequestParam Long gameId) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        validatePlayer(player, game);
        return turnAvailableWordsDtoConverter.convert(game);
    }

    @PutMapping("/current")
    public ResponseEntity<Void> setCurrentGuessing(Principal principal, @RequestParam Long gameId,
                                                    @RequestParam String currentGuessing) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        validatePlayer(player, game);
        gameService.setCurrentGuessing(game, currentGuessing);
        return ResponseEntity.ok().build();
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
        if (!team.getGame().equals(game) ||
                !team.getPlayers().contains(player)) {
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
    public ResponseEntity<Void> markTurnWord(Principal principal, @RequestParam Long gameId, @RequestParam Long teamId,
                                               @RequestParam String word, @RequestParam boolean isGuessed,
                                             @RequestParam(required = false) String currentGuessing) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        Team team = teamService.getTeamOrThrow(teamId);
        validatePlayer(player, game, team);
        gameService.markTurnWordAndFinishIfWordsCompleted(game, word, isGuessed, currentGuessing);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/finish")
    public ResponseEntity<Set<TurnApprovingWordDto>> finishTurn(Principal principal, @RequestParam Long gameId) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        validatePlayer(player, game);
        gameService.finishTurn(game, false);
        return ResponseEntity.ok(
                Objects.requireNonNull(
                        turnApprovingWordsDtoConverter.convert(
                                gameWordService.getCurrentTurnWords(gameId))));
    }

    @PutMapping("/approve")
    public ResponseEntity<Void> approveTurn(Principal principal, @RequestParam Long gameId,
                                            @RequestParam Long teamId, @RequestParam Set<String> guessedWords) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        Team team = teamService.getTeamOrThrow(teamId);
        validatePlayer(player, game, team);
        Team lastTeamTurn = game.getTeamTurn();
        gameService.approveTurn(gameId, guessedWords, team);
        teamService.movePlayerTurn(lastTeamTurn);
        teamService.addScore(team.getId(), guessedWords.size());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/words/approving")
    public ResponseEntity<Set<TurnApprovingWordDto>> getCurrentTurnWords(Principal principal, @RequestParam Long gameId) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        validatePlayer(player, game);
        return ResponseEntity.ok(
                Objects.requireNonNull(
                        turnApprovingWordsDtoConverter.convert(
                                gameWordService.getCurrentTurnWords(gameId))));
    }

}
