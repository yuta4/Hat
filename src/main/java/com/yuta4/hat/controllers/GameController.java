package com.yuta4.hat.controllers;

import com.yuta4.hat.Level;
import com.yuta4.hat.dto.JoinGameDto;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.entities.Word;
import com.yuta4.hat.exceptions.NoSuchGameException;
import com.yuta4.hat.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/game")
public class GameController {

    private GameService gameService;
    private PlayerService playerService;
    private WordService wordService;
    private TeamService teamService;
    private GameWordService gameWordService;

    public GameController(GameService gameService, PlayerService playerService, WordService wordService,
                          TeamService teamService, GameWordService gameWordService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.wordService = wordService;
        this.teamService = teamService;
        this.gameWordService = gameWordService;
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
        if(game != null && !Boolean.FALSE.equals(game.getIsActive())) {
            return ResponseEntity.ok().body(game.getId());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("unwatch")
    public ResponseEntity<Boolean> removeWatcher(Principal principal, @RequestParam Long gameId) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        gameService.unwatch(gameId, player);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/finish")
    public ResponseEntity<Void> finishGame(Principal principal, @RequestParam Long gameId) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = gameService.getGameById(gameId);
        gameService.finishGameIfPermitted(game, player);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate/words")
    public ResponseEntity<String> generateWords(Principal principal, @RequestParam Integer wordsPerPlayer,
                                                @RequestParam(required = false) List<Level> levels) {
        try {
            Player player = playerService.getPlayerByLogin(principal.getName());
            Game game = player.getLastGame();
            List<Team> gameTeams = teamService.getGameTeams(game);

            int wordsRequired = gameTeams.stream()
                    .map(team -> team.getPlayers().size() * wordsPerPlayer)
                    .reduce(Integer::sum)
                    .orElse(0);
            if(wordsRequired == 0 || (game.getWords() != null && !game.getWords().isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to generate words");
            }
            List<Word> generatedWords = wordService.generateRandomWordsByLevels(wordsRequired, levels);
            gameWordService.convertFromWordsAndPersist(game, generatedWords);
            return ResponseEntity.ok().build();
        } catch (NoSuchGameException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/round/start")
    public ResponseEntity<List<String>> startRound(Principal principal) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        Game game = player.getLastGame();
        //TODO start timer

        return ResponseEntity.ok(gameWordService.getNotGuessedWords(game));
    }

    @PutMapping("/round/finish")
    public ResponseEntity<List<String>> finishRound(Principal principal, @RequestParam List<String> guessedWords) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        gameWordService.markAsGuessed(guessedWords, teamService.getPlayerTeam(player));
        teamService.movePlayerTurn(player);
        gameService.moveTeamTurn(player.getLastGame());
        return ResponseEntity.ok().build();
    }

    @GetMapping("notStarted")
    public Set<JoinGameDto> getNotStartedGames() {
        return gameService.getNotStartedGames().stream()
                .map(JoinGameDto::new)
                .collect(Collectors.toSet());
    }

}
