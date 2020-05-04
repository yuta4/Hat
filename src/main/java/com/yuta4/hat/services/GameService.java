package com.yuta4.hat.services;

import com.yuta4.hat.GameProgress;
import com.yuta4.hat.Language;
import com.yuta4.hat.Level;
import com.yuta4.hat.TurnStatus;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.events.NewGameEvent;
import com.yuta4.hat.exceptions.GameNotFoundException;
import com.yuta4.hat.exceptions.RequestValidationException;
import com.yuta4.hat.exceptions.TurnException;
import com.yuta4.hat.repositories.GameRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

@Service
public class GameService {

    private GameRepository gameRepository;
    private ApplicationListener<NewGameEvent> newGamesListener;
    private GameWordService gameWordService;

    public GameService(GameRepository gameRepository, ApplicationListener<NewGameEvent> newGamesListener, GameWordService gameWordService) {
        this.gameRepository = gameRepository;
        this.newGamesListener = newGamesListener;
        this.gameWordService = gameWordService;
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
        validateGameOwner(player, game, "Only game owner can finish the game");
        game.setIsActive(false);
        game.setGameProgress(GameProgress.SUMMERY_VIEW);
        gameRepository.save(game);
        newGamesListener.onApplicationEvent(new NewGameEvent(player));
    }

    public boolean changeGameProgress(Player player, Game game, GameProgress gameProgress) {
        if (game.getOwner().equals(player)) {
            gameProgress.proceedGameProgress(game);
            game.setGameProgress(gameProgress);
            gameRepository.save(game);
            return true;
        }
        return false;
    }

    public void moveTeamTurn(Game game) {
        Team team = game.getTeamTurn();
        Set<Team> teams = game.getTeams();
        Iterator<Team> teamIterator = teams.iterator();
        Team firstTeam = teamIterator.next();
        Team nextTeam = firstTeam;
        if (team != null) {
            while (nextTeam != team) {
                nextTeam = teamIterator.next();
            }
            if (teamIterator.hasNext()) {
                nextTeam = teamIterator.next();
            } else {
                nextTeam = firstTeam;
            }
        }
        game.setTeamTurn(nextTeam);
        gameRepository.save(game);
    }

    public Game getGameById(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
    }

    public boolean addWatcher(Game game, Player playerToAdd, Set<Player> gamePlayers) {
        if (!gamePlayers.contains(playerToAdd)) {
            game.getWatchers().add(playerToAdd);
            gameRepository.save(game);
            return true;
        }
        return false;
    }

    public boolean removeWatcher(Long gameId, Player playerToRemove) {
        Game game = getGameById(gameId);
        if (game.getWatchers().contains(playerToRemove)) {
            game.getWatchers().remove(playerToRemove);
            gameRepository.save(game);
            return true;
        }
        return false;
    }

    public Set<Game> getNotStartedGames() {
        return gameRepository.findGamesByIsActiveIsNullOrderByIdDesc();
    }

    public void addTeam(Game game, Team newTeam) {
        game.getTeams().add(newTeam);
        gameRepository.save(game);
    }

    public void deleteTeam(Team teamToDelete) {
        Game game = teamToDelete.getGame();
        game.getTeams().remove(teamToDelete);
        if(teamToDelete.equals(game.getTeamTurn())) {
            game.setTeamTurn(null);
        }
        gameRepository.save(game);
    }

    public void setLanguageProp(Player player, Long gameId, String languageDisplayName, boolean value) {
        Game game = getGameById(gameId);
        validateGameOwner(player, game, "Only game owner can change game properties");
        Language language = Language.getByDisplayName(languageDisplayName);
        if (value) {
            game.getWordsLanguages().add(language);
        } else {
            game.getWordsLanguages().remove(language);
        }
        gameRepository.save(game);
    }

    public void setLevelProp(Player player, Long gameId, String levelDisplayName, boolean value) {
        Game game = getGameById(gameId);
        validateGameOwner(player, game, "Only game owner can change game properties");
        Level language = Level.getByDisplayName(levelDisplayName);
        if (value) {
            game.getWordsLevels().add(language);
        } else {
            game.getWordsLevels().remove(language);
        }
        gameRepository.save(game);
    }

    public void setWordsPerPlayer(Player player, Long gameId, Integer value) {
        Game game = getGameById(gameId);
        validateGameOwner(player, game, "Only game owner can change game properties");
        game.setWordsPerPlayer(value);
        gameRepository.save(game);
    }

    private void validateGameOwner(Player player, Game game, String s) {
        if (!game.getOwner().equals(player)) {
            throw new RequestValidationException(s);
        }
    }

    public void unPauseTurn(Long gameId) {
        Game game = getGameById(gameId);
        if(!TurnStatus.PAUSED.equals(game.getTurnStatus()) ||
            game.getTurnEndTime() == null || game.getPausedTimeRemains() == null) {
            throw new TurnException(String.format("Can't unpause turn %d : %s, %s, %s ",
                    gameId, game.getTurnStatus(), game.getTurnEndTime(), game.getPausedTimeRemains()));
        }
        game.setTurnEndTime(LocalDateTime.now().plus(game.getPausedTimeRemains()));
        game.setTurnStatus(TurnStatus.ACTIVE);
        game.setPausedTimeRemains(null);
        gameRepository.save(game);
    }

    public void pauseTurn(Long gameId) {
        Game game = getGameById(gameId);
        if(!TurnStatus.ACTIVE.equals(game.getTurnStatus()) ||
                game.getTurnEndTime() == null || game.getPausedTimeRemains() != null) {
            throw new TurnException(String.format("Can't pause turn %d : %s, %s, %s ",
                    gameId, game.getTurnStatus(), game.getTurnEndTime(), game.getPausedTimeRemains()));
        }
        game.setPausedTimeRemains(Duration.between(LocalDateTime.now(), game.getTurnEndTime()));
        game.setTurnStatus(TurnStatus.PAUSED);
        gameRepository.save(game);
    }

    public void startTurn(Long gameId) {
        Game game = getGameById(gameId);
        if(!TurnStatus.NOT_STARTED.equals(game.getTurnStatus()) ||
                game.getTurnEndTime() != null || game.getPausedTimeRemains() != null) {
            throw new TurnException(String.format("Can't start turn %d : %s, %s, %s ",
                    gameId, game.getTurnStatus(), game.getTurnEndTime(), game.getPausedTimeRemains()));
        }
        game.setTurnEndTime(LocalDateTime.now().plus(Duration.ofMinutes(1)));
        game.setTurnStatus(TurnStatus.ACTIVE);
        gameRepository.save(game);
    }

    public void finishTurn(Long gameId) {
        Game game = getGameById(gameId);
        if(!TurnStatus.ACTIVE.equals(game.getTurnStatus()) ||
            game.getTurnEndTime() == null ||
                game.getTurnEndTime().isAfter(LocalDateTime.now())) {
            throw new TurnException(String.format("Can't finish turn %d : %s, %s",
                    gameId, game.getTurnStatus(), game.getTurnEndTime()));
        }
        game.setTurnStatus(TurnStatus.APPROVING);
        game.setTurnEndTime(null);
        gameRepository.save(game);
    }

    public void approveTurn(Long gameId, Set<String> guessedWords, Team playerTeam) {
        Game game = getGameById(gameId);
        if(!TurnStatus.APPROVING.equals(game.getTurnStatus()) ||
                game.getTurnEndTime() != null) {
            throw new TurnException(String.format("Can't approve turn %d : %s, %s",
                    gameId, game.getTurnStatus(), game.getTurnEndTime()));
        }
        moveTeamTurn(game);
        gameWordService.markAsGuessed(game, guessedWords, playerTeam);
        game.setTurnStatus(TurnStatus.NOT_STARTED);
        gameRepository.save(game);
    }
}
