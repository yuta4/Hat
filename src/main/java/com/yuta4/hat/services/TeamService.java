package com.yuta4.hat.services;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.exceptions.TeamException;
import com.yuta4.hat.repositories.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private TeamRepository teamRepository;
    private GameService gameService;
    private PlayerService playerService;

    public TeamService(TeamRepository teamRepository, GameService gameService, PlayerService playerService) {
        this.teamRepository = teamRepository;
        this.gameService = gameService;
        this.playerService = playerService;
    }

    public Team createTeam(Game game, String name) {
        Team team = new Team();
        team.setGame(game);
        team.setName(name);
        gameService.addTeam(game, team);
        return team;
    }

    private void validateAddingPlayersToTeam(List<Team> sameGameTeams, Player newPlayer) {
        if (newPlayer.getLastGame() != null &&
                Boolean.TRUE.equals(newPlayer.getLastGame().getIsActive())) {
            throw new TeamException(
                    String.format("Can't add player %s to the team because he has not finished game", newPlayer));
        }
        sameGameTeams.forEach(team -> {
            if (team.getPlayers().contains(newPlayer)) {
                throw new TeamException(
                        String.format("%s can't be added to new team as he is already present in team %s",
                                newPlayer, team.getPlayers()));
            }
        });
    }

    public List<Team> getGameTeams(Game game) {
        return teamRepository.findTeamsByGame(game);
    }

    public Team getPlayerTeam(Player player) {
        return player.getLastGame().getTeams().stream()
                .filter(team -> team.getPlayers().contains(player))
                .findFirst().orElseThrow(() -> new TeamException("Can't find team for player " + player));
    }

    public void movePlayerTurn(Team team) {
        Player lastTurn = team.getPlayerTurn();
        Set<Player> teamPlayers = team.getPlayers();
        Iterator<Player> teamPlayersIterator = teamPlayers.iterator();
        Player firstPlayer = teamPlayersIterator.next();
        Player nextPlayer = firstPlayer;
        if(lastTurn != null) {
            while (nextPlayer != lastTurn) {
                nextPlayer = teamPlayersIterator.next();
            }
            if (teamPlayersIterator.hasNext()) {
                nextPlayer = teamPlayersIterator.next();
            } else {
                nextPlayer = firstPlayer;
            }
        }
        team.setPlayerTurn(nextPlayer);
        teamRepository.save(team);
    }

    public Boolean deleteTeam(Long teamId) {
        Team teamToDelete = getTeamOrThrow(teamId);
        gameService.deleteTeam(teamToDelete);
        return true;
    }

    public Team getTeamOrThrow(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new TeamException("No team with id found : " + teamId));
    }

    public Set<Player> getGamePlayers(Game game) {
        return getGameTeams(game).stream()
                .flatMap(team -> team.getPlayers().stream())
                .collect(Collectors.toSet());
    }

    public Boolean addPlayerToTeam(Team team, Player newPlayer) {
        Game game = team.getGame();
        validateAddingPlayersToTeam(getGameTeams(game), newPlayer);
        team.getPlayers().add(newPlayer);
        playerService.setLastGame(newPlayer, game);
        if (!gameService.removeWatcher(game.getId(), newPlayer)) {
            teamRepository.save(team);
        }
        return true;
    }

    public Boolean removePlayerFromTeam(Team team, Player playerToRemove) {
        boolean isRemoved = team.getPlayers().remove(playerToRemove);
        Game game = team.getGame();
        if(playerToRemove.equals(team.getPlayerTurn())) {
            team.setPlayerTurn(null);
        }
        if (!gameService.addWatcher(game, playerToRemove, getGamePlayers(game))) {
            teamRepository.save(team);
        }
        return isRemoved;
    }

    public void addScore(Long id, long scoreToAdd) {
        Team team = getTeamOrThrow(id);
        team.setScore(team.getScore() + scoreToAdd);
        teamRepository.save(team);
    }
}
