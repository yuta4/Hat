package com.yuta4.hat.services;

import com.yuta4.hat.exceptionas.NoSuchPlayerException;
import com.yuta4.hat.PlayerPrincipal;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import com.yuta4.hat.repositories.PlayerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerService implements UserDetailsService {

    private PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository1) {
        this.playerRepository = playerRepository1;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Player user = playerRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        return new PlayerPrincipal(user);
    }

    public Player getPlayerByEmail(String email) {
        return playerRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public void setLastGame(Player player, Game game) {
        player.setLastGame(game);
        playerRepository.save(player);
    }

    public List<Player> getPLayersList(String playerEmails) {
        String[] emails = playerEmails.split(",");
        List<Player> players = new ArrayList<>();
        for(String email : emails) {
            Player player = playerRepository.findByEmail(email).orElseThrow(() -> new NoSuchPlayerException(email));

            players.add(player);
        }
        return players;
    }
}
