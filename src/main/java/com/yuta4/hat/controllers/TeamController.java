package com.yuta4.hat.controllers;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.exceptions.RequestValidationException;
import com.yuta4.hat.exceptions.TeamException;
import com.yuta4.hat.services.GameService;
import com.yuta4.hat.services.PlayerService;
import com.yuta4.hat.services.RequestValidationService;
import com.yuta4.hat.services.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/team")
public class TeamController {

    private PlayerService playerService;
    private GameService gameService;
    private TeamService teamService;
    private RequestValidationService requestValidationService;

    public TeamController(PlayerService playerService, GameService gameService, TeamService teamService,
                          RequestValidationService requestValidationService) {
        this.playerService = playerService;
        this.gameService = gameService;
        this.teamService = teamService;
        this.requestValidationService = requestValidationService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTeam(Principal principal, @RequestParam Long gameId) {
        try {
            Player player = playerService.getPlayerByLogin(principal.getName());
            Game game = player.getLastGame();
            requestValidationService.validate(gameId, player, game);
            return ResponseEntity.ok(teamService.createTeam(game).toString());
        } catch (RequestValidationException | UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTeam(Principal principal, @RequestParam Long gameId, @RequestParam Long teamId) {
        try {
            Player player = playerService.getPlayerByLogin(principal.getName());
            Game game = player.getLastGame();
            requestValidationService.validate(gameId, player, game);
            return ResponseEntity.ok(teamService.deleteTeam(teamId).toString());
        } catch (UsernameNotFoundException | TeamException | RequestValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/extend")
    public ResponseEntity<String> extendTeam(Principal principal, @RequestParam String newPlayerLogin,
                                             @RequestParam Long teamId) {
        try {
            Player player = playerService.getPlayerByLogin(principal.getName());
            Player newTeamPlayer = playerService.getPlayerByLogin(newPlayerLogin);
            Team team = teamService.getTeamOrThrow(teamId);
            requestValidationService.validateTeamExtension(player, player, team);
            return ResponseEntity.ok(teamService.addPlayerToTeam(team, newTeamPlayer).toString());
        } catch (UsernameNotFoundException | TeamException | RequestValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/reduce")
    public ResponseEntity<String> reduceTeam(Principal principal, @RequestParam String playerLogin,
                                             @RequestParam Long teamId,
                                             @RequestParam(required = false) boolean moveToWatchers) {
        try {
            Player requester = playerService.getPlayerByLogin(principal.getName());
            Player playerToRemove = playerService.getPlayerByLogin(playerLogin);
            Team team = teamService.getTeamOrThrow(teamId);
            requestValidationService.validateTeamReduction(requester, playerToRemove, team);
            Boolean result = teamService.removePlayerFromTeam(team, playerToRemove);
            if(moveToWatchers) {
                gameService.addWatcher(team.getGame(), playerToRemove, teamService.getGamePlayers(team.getGame()));
            }
            return ResponseEntity.ok(result.toString());
        } catch (UsernameNotFoundException | TeamException | RequestValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

}
