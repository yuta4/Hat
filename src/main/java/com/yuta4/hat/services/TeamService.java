package com.yuta4.hat.services;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.exceptionas.TeamException;
import com.yuta4.hat.repositories.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private TeamRepository teamRepository;
    private PlayerService playerService;
    private GameService gameService;

    public TeamService(TeamRepository teamRepository, PlayerService playerService, GameService gameService) {
        this.teamRepository = teamRepository;
        this.playerService = playerService;
        this.gameService = gameService;
    }

    public Long createTeam(Game game, List<Player> newPlayers) {
        validateAddingPlayersToTeam(teamRepository.findTeamsByGame(game), newPlayers);
        Team team = new Team();
        team.setGame(game);
        team.setPlayers(newPlayers);
        newPlayers.forEach(player -> playerService.setLastGame(player, game));
        return teamRepository.save(team).getId();
    }

    private void validateAddingPlayersToTeam(List<Team> sameGameTeams, List<Player> newPlayers) {
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
        List<Player> teamPlayers = team.getPlayers();
        Player nextPlayer = teamPlayers.indexOf(player) + 1 < teamPlayers.size() ?
                teamPlayers.get(teamPlayers.indexOf(player) + 1) : teamPlayers.get(0);
        team.setPlayerTurn(nextPlayer);
        teamRepository.save(team);
    }

}
