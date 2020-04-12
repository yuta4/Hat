package com.yuta4.hat.services;

import com.yuta4.hat.GameProgress;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.exceptions.GameNotFoundException;
import com.yuta4.hat.exceptions.RequestValidationException;
import com.yuta4.hat.repositories.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameService {

    private GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createGame(Player player) {
        Game game = new Game();
        game.setOwner(player);
        game.setGameProgress(GameProgress.TEAMS_FORMATION);
        return gameRepository.save(game);
    }

    public void finishGameIfPermitted(Game game, Player player) {
        if(!game.getOwner().equals(player)) {
            throw new RequestValidationException("Only game owner can finish the game");
        }
        game.setIsActive(false);
        gameRepository.save(game);
    }

    public void saveGameProgress(Game game, GameProgress gameProgress) {
        game.setGameProgress(gameProgress);
        gameRepository.save(game);
    }

    public void moveTeamTurn(Game game) {
        Team team = game.getTeamTurn();
        List<Team> teams = game.getTeams();
        Team nextTeam = teams.indexOf(team) + 1 < teams.size() ? teams.get(teams.indexOf(team) + 1) : teams.get(0);
        game.setTeamTurn(nextTeam);
        gameRepository.save(game);
    }

    public Game getGameById(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
    }

    public Set<String> addAndGetWatchersLogin(Long gameID, Player playerToAdd, Set<Player> players) {
        Game game = getGameById(gameID);
        if(!game.getOwner().equals(playerToAdd) && !players.contains(playerToAdd)) {
            game.getWatchers().add(playerToAdd);
            gameRepository.save(game);
        }
        return game.getWatchers().stream()
                .map(Player::getLogin)
                .collect(Collectors.toSet());
    }

    public Set<Game> getNotStartedGames() {
        return gameRepository.findGamesByIsActiveIsNull();
    }

    public void unwatch(Long gameId, Player player) {
        Game game = getGameById(gameId);
        if(game.getWatchers().remove(player)) {
            gameRepository.save(game);
        }
    }
}
