package com.yuta4.hat.controllers;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.exceptionas.NoSuchPlayerException;
import com.yuta4.hat.exceptionas.TeamException;
import com.yuta4.hat.services.GameService;
import com.yuta4.hat.services.PlayerService;
import com.yuta4.hat.services.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Set;

@Controller
@RequestMapping("/team")
public class TeamController {

    private GameService gameService;
    private PlayerService playerService;
    private TeamService teamService;

    public TeamController(PlayerService playerService, TeamService teamService, GameService gameService) {
        this.playerService = playerService;
        this.teamService = teamService;
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTeam(Principal principal, @RequestParam String playerEmails) {
        try {
            Player player = playerService.getPlayerByEmail(principal.getName());
            Game game = player.getLastGame();
            Set<Player> players = playerService.getPLayersList(playerEmails);
            return ResponseEntity.ok(teamService.createTeam(game, players).toString());
        } catch (NoSuchPlayerException | TeamException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

}
