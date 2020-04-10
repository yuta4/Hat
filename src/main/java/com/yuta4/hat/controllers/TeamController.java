package com.yuta4.hat.controllers;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.exceptions.RequestValidationException;
import com.yuta4.hat.exceptions.TeamException;
import com.yuta4.hat.services.PlayerService;
import com.yuta4.hat.services.RequestValidationService;
import com.yuta4.hat.services.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/team")
public class TeamController {

    private PlayerService playerService;
    private TeamService teamService;
    private RequestValidationService requestValidationService;

    public TeamController(PlayerService playerService, TeamService teamService,
                          RequestValidationService requestValidationService) {
        this.playerService = playerService;
        this.teamService = teamService;
        this.requestValidationService = requestValidationService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTeam(Principal principal, @RequestParam Long gameID) {
        try {
            Player player = playerService.getPlayerByEmail(principal.getName());
            Game game = player.getLastGame();
            requestValidationService.validate(gameID, player, game);
            return ResponseEntity.ok(teamService.createTeam(game).toString());
        } catch (RequestValidationException | UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTeam(Principal principal, @RequestParam Long gameId, @RequestParam Long teamId) {
        try {
            Player player = playerService.getPlayerByEmail(principal.getName());
            Game game = player.getLastGame();
            requestValidationService.validate(gameId, player, game);
            return ResponseEntity.ok(teamService.deleteTeam(teamId).toString());
        } catch (UsernameNotFoundException | TeamException | RequestValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/extend")
    public ResponseEntity<String> extendTeam(Principal principal, @RequestParam String playerEmail,
                                             @RequestParam Long teamId, @RequestParam Long gameId) {
        try {
            Player player = playerService.getPlayerByEmail(principal.getName());
            Game game = player.getLastGame();
            requestValidationService.validate(gameId, player, game);
            Player newTeamPlayer = playerService.getPlayerByEmail(playerEmail);
            Team team = teamService.getTeamOrThrow(teamId);
            return ResponseEntity.ok(teamService.addPlayerToTeam(team, newTeamPlayer).toString());
        } catch (UsernameNotFoundException | TeamException | RequestValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }



}
