package com.yuta4.hat.controllers;

import com.yuta4.hat.entities.Player;
import com.yuta4.hat.services.GameService;
import com.yuta4.hat.services.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/words")
@Slf4j
public class WordsController {
    private final PlayerService playerService;
    private final GameService gameService;

    public WordsController(PlayerService playerService, GameService gameService) {
        this.playerService = playerService;
        this.gameService = gameService;
    }

    @PutMapping("/language")
    public ResponseEntity<Void> setWordsLang(Principal principal, @RequestParam Long gameId,
                                             @RequestParam String language, @RequestParam boolean value) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        gameService.setLanguageProp(player, gameId, language, value);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/level")
    public ResponseEntity<Void> setWordsLevel(Principal principal, @RequestParam Long gameId,
                                              @RequestParam String level, @RequestParam boolean value) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        gameService.setLevelProp(player, gameId, level, value);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/amount")
    public ResponseEntity<Void> setWordsAmount(Principal principal, @RequestParam Long gameId,
                                               @RequestParam Integer value) {
        Player player = playerService.getPlayerByLogin(principal.getName());
        gameService.setWordsPerPlayer(player, gameId, value);
        return ResponseEntity.ok().build();
    }
}
