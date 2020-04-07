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

@Service
public class TeamService {

    private TeamRepository teamRepository;
    private PlayerService playerService;

    public TeamService(TeamRepository teamRepository, PlayerService playerService) {
        this.teamRepository = teamRepository;
        this.playerService = playerService;
    }

    public Long createTeam(Game game, Set<Player> newPlayers) {
        validateAddingPlayersToTeam(teamRepository.findTeamsByGame(game), newPlayers);
        Team team = new Team();
        team.setGame(game);
        team.setPlayers(newPlayers);
        newPlayers.forEach(player -> playerService.setLastGame(player, game));
        return teamRepository.save(team).getId();
    }

    private void validateAddingPlayersToTeam(List<Team> sameGameTeams, Set<Player> newPlayers) {
        if(newPlayers.size() < 2) {
            throw new TeamException("Team should contain more then one player");
        }
        newPlayers.forEach(newPlayer -> {
            if(Boolean.TRUE.equals(newPlayer.getLastGame().getIsActive())) {
                throw new TeamException(
                        String.format("Can't add player %s to the team because he has not finished game", newPlayer));
            }
        });
        sameGameTeams.forEach(team -> newPlayers.forEach(newPlayer -> {
            if(team.getPlayers().contains(newPlayer)) {
                throw new TeamException(
                        String.format("%s can't be added to new team as he is already present in team %s",
                                newPlayer, team.getPlayers()));
            }
        }));
    }

    public List<Team> getGameTeams(Game game) {
        return teamRepository.findTeamsByGame(game);
    }

    public Team getPlayerTeam(Player player) {
        return player.getLastGame().getTeams().stream()
                .filter(team -> team.getPlayers().contains(player))
                .findFirst().orElseThrow(() -> new TeamException("Can't find team for player " + player));
    }

    public void movePlayerTurn(Player player) {
        Team team = getPlayerTeam(player);
        Set<Player> teamPlayers = team.getPlayers();
        Iterator<Player> teamPlayersIterator = teamPlayers.iterator();
        Player firstPlayer = teamPlayersIterator.next();
        Player nextPlayer = firstPlayer;
        while (nextPlayer != player) {
            nextPlayer = teamPlayersIterator.next();
        }
        if(teamPlayersIterator.hasNext()) {
            nextPlayer = teamPlayersIterator.next();
        } else {
            nextPlayer = firstPlayer;
        }
        team.setPlayerTurn(nextPlayer);
        teamRepository.save(team);
    }

}
