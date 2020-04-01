package com.yuta4.hat.controllers;

import com.yuta4.hat.entities.Player;
import com.yuta4.hat.repositories.PlayerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PlayerController {

    private PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @GetMapping("/players")
    public @ResponseBody Iterable<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
}
