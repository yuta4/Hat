package com.yuta4.hat.controllers;

import com.yuta4.hat.Level;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.entities.Word;
import com.yuta4.hat.exceptionas.NoSuchGameException;
import com.yuta4.hat.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
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

    @PostMapping("/create")
    public ResponseEntity<Long> startGame(Principal principal) {
        Player player = playerService.getPlayerByEmail(principal.getName());
        Game game = player.getLastGame();
        if(game == null || Boolean.FALSE.equals(game.getIsActive())) {
            game = gameService.createGame(player);
            playerService.setLastGame(player, game);
        }
        return ResponseEntity.ok().body(game.getId());
    }

    @PostMapping("/finish")
    public ResponseEntity<Void> finishGame(Principal principal) {
        Player player = playerService.getPlayerByEmail(principal.getName());
        Game game = player.getLastGame();
        gameService.finishGame(game);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate/words")
    public ResponseEntity<String> generateWords(Principal principal, @RequestParam Integer wordsPerPlayer,
                                                @RequestParam(required = false) List<Level> levels) {
        try {
            Player player = playerService.getPlayerByEmail(principal.getName());
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

    @PostMapping("/round/start")
    public ResponseEntity<List<String>> startRound(Principal principal) {
        Player player = playerService.getPlayerByEmail(principal.getName());
        Game game = player.getLastGame();
        //TODO start timer

        return ResponseEntity.ok(gameWordService.getNotGuessedWords(game));
    }

    @PostMapping("/round/finish")
    public ResponseEntity<List<String>> finishRound(Principal principal, @RequestParam List<String> guessedWords) {
        Player player = playerService.getPlayerByEmail(principal.getName());
        gameWordService.markAsGuessed(guessedWords, teamService.getPlayerTeam(player));
        teamService.movePlayerTurn(player);
        gameService.moveTeamTurn(player.getLastGame());
        return ResponseEntity.ok().build();
    }

}
