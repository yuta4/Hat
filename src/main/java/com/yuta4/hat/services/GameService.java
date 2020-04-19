package com.yuta4.hat.services;

import com.yuta4.hat.GameProgress;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.events.NewGameEvent;
import com.yuta4.hat.exceptions.GameNotFoundException;
import com.yuta4.hat.exceptions.RequestValidationException;
import com.yuta4.hat.repositories.GameRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class GameService {

    private GameRepository gameRepository;
    private ApplicationListener<NewGameEvent> newGamesListener;

    public GameService(GameRepository gameRepository, ApplicationListener<NewGameEvent> newGamesListener) {
        this.gameRepository = gameRepository;
        this.newGamesListener = newGamesListener;
    }

    public Game createGame(Player player) {
        Game game = new Game();
        game.setOwner(player);
        game.setGameProgress(GameProgress.TEAMS_FORMATION);
        game.setWatchers(Collections.singleton(player));
        gameRepository.save(game);
        newGamesListener.onApplicationEvent(new NewGameEvent(player));
        return game;
    }

    public void finishGameIfPermitted(Game game, Player player) {
        if(!game.getOwner().equals(player)) {
            throw new RequestValidationException("Only game owner can finish the game");
        }
        game.setIsActive(false);
        gameRepository.save(game);
        newGamesListener.onApplicationEvent(new NewGameEvent(player));
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

    public boolean addWatcher(Game game, Player playerToAdd, Set<Player> gamePlayers) {
        if(!gamePlayers.contains(playerToAdd)) {
            game.getWatchers().add(playerToAdd);
            gameRepository.save(game);
            return true;
        }
        return false;
    }

    public boolean removeWatcher(Long gameId, Player playerToRemove) {
        Game game = getGameById(gameId);
        if(game.getWatchers().contains(playerToRemove)) {
            game.getWatchers().remove(playerToRemove);
            gameRepository.save(game);
            return true;
        }
        return false;
    }

    public Set<Game> getNotStartedGames() {
        return gameRepository.findGamesByIsActiveIsNullOrderByIdDesc();
    }
}
