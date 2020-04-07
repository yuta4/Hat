package com.yuta4.hat.controllers;

import com.yuta4.hat.entities.Player;
import com.yuta4.hat.repositories.PlayerRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<String> getAllPlayerEmails() {
        Iterable<Player> all = playerRepository.findAll();
        Set<String> emails = new HashSet<>();
        all.forEach(player -> emails.add(player.getEmail()));
        return emails;
    }
}
