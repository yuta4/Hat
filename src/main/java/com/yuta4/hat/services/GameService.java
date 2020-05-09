package com.yuta4.hat.services;

import com.yuta4.hat.GameProgress;
import com.yuta4.hat.Language;
import com.yuta4.hat.Level;
import com.yuta4.hat.TurnStatus;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.GameWord;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.events.NewGameEvent;
import com.yuta4.hat.exceptions.GameNotFoundException;
import com.yuta4.hat.exceptions.RequestValidationException;
import com.yuta4.hat.exceptions.TurnException;
import com.yuta4.hat.exceptions.WordException;
import com.yuta4.hat.repositories.GameRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
        if(changeGameProgress(player, game, GameProgress.SUMMERY_VIEW)) {
            newGamesListener.onApplicationEvent(new NewGameEvent(player));
        } else {
            throw new RequestValidationException("Only game owner can finish the game");
        }
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
        if (teamToDelete.equals(game.getTeamTurn())) {
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

    public Set<String> getAvailableWords(Game game, boolean currentTurn) {
        return game.getWords().stream()
                .filter(gameWord -> gameWord.getTeam() == null)
                .filter(gameWord -> !currentTurn ||
                        gameWord.getCurrentTurnGuessed() == null)
                .map(gameWord -> gameWord.getWord().getString())
                .collect(Collectors.toSet());
    }

    //TODO: rename
    public void markTurnWordAndFinishIfWordsCompleted(Game game, Team team, String word, boolean isGuessed,
                                                      String currentGuessing) {
        GameWord gameWord = findGameWord(game, word);
        Optional.ofNullable(currentGuessing)
                .map(str -> findGameWord(game, str))
                .ifPresent(game::setTurnCurrentGuessing);
        gameWord.setCurrentTurnGuessed(isGuessed);
        gameRepository.save(game);
        if(getAvailableWords(game, true).isEmpty()) {
            finishTurn(game, true);
        }
    }

    private GameWord findGameWord(Game game, String word) {
        return game.getWords().stream()
                .filter(gw -> gw.getWord().getString().equals(word))
                .findFirst()
                .orElseThrow(() -> new WordException(String.format("Can't find word %s in game %d", word, game.getId())));
    }

    public void unPauseTurn(Long gameId) {
        Game game = getGameById(gameId);
        if (!TurnStatus.PAUSED.equals(game.getTurnStatus()) ||
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
        if (!TurnStatus.ACTIVE.equals(game.getTurnStatus()) ||
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
        if (!TurnStatus.NOT_STARTED.equals(game.getTurnStatus()) ||
                game.getTurnEndTime() != null || game.getPausedTimeRemains() != null) {
            throw new TurnException(String.format("Can't start turn %d : %s, %s, %s ",
                    gameId, game.getTurnStatus(), game.getTurnEndTime(), game.getPausedTimeRemains()));
        }
        game.setTurnEndTime(LocalDateTime.now().plus(Duration.ofMinutes(1)));
        game.setTurnStatus(TurnStatus.ACTIVE);
        gameRepository.save(game);
    }

    public void finishTurn(Game game, boolean noWordsLeft) {
        if (!TurnStatus.ACTIVE.equals(game.getTurnStatus()) ||
                game.getTurnEndTime() == null ||
                (!noWordsLeft &&
                        game.getTurnEndTime().minusSeconds(1).isAfter(LocalDateTime.now()))) {
            throw new TurnException(String.format("Can't finish turn %d : %s, %s, %s",
                    game.getId(), game.getTurnStatus(), game.getTurnEndTime(), noWordsLeft));
        }
        game.setTurnStatus(TurnStatus.APPROVING);
        game.setTurnEndTime(null);
        gameRepository.save(game);
    }

    public void approveTurn(Long gameId, Set<String> guessedWords, Team playerTeam) {
        Game game = getGameById(gameId);
        if (!TurnStatus.APPROVING.equals(game.getTurnStatus()) ||
                game.getTurnEndTime() != null) {
            throw new TurnException(String.format("Can't approve turn %d : %s, %s",
                    gameId, game.getTurnStatus(), game.getTurnEndTime()));
        }
        moveTeamTurn(game);
        game.setTurnStatus(TurnStatus.NOT_STARTED);
        if(markGuessedAndClearTurnWords(game, guessedWords, playerTeam)) {
            changeGameProgress(game.getOwner(), game, game.getGameProgress().getNext());
        } else {
            gameRepository.save(game);
        }
    }

    /**
     *
     * @return true if all words are guessed
     */
    private boolean markGuessedAndClearTurnWords(Game game, Set<String> guessedWords, Team team) {
        AtomicBoolean allGuessed = new AtomicBoolean(true);
        game.getWords()
                .forEach(word -> {
                    if(guessedWords.contains(word.getWord().getString())) {
                        word.setTeam(team);
                    }
                    word.setCurrentTurnGuessed(null);
                    if(word.getTeam() == null) {
                        allGuessed.set(false);
                    }
                });
        game.setTurnCurrentGuessing(null);
        return allGuessed.get();
    }

    public void clearWatchers(Long id) {
        Game game = getGameById(id);
        game.getWatchers().clear();
        gameRepository.save(game);
    }

    public void setAllowSkipWords(Player player, Long gameId, boolean value) {
        Game game = getGameById(gameId);
        validateGameOwner(player, game, "Only game owner can change game properties");
        game.setAllowSkipWords(value);
        gameRepository.save(game);
    }

    public void setCurrentGuessing(Game game, String currentGuessing) {
        GameWord gameWord = findGameWord(game, currentGuessing);
        game.setTurnCurrentGuessing(gameWord);
        gameRepository.save(game);
    }
}
