package com.yuta4.hat.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuta4.hat.GameProgress;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Data
@ToString(of = {"id", "owner", "isActive", "gameProgress", "teams", "teamTurn"})
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "game", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<GameWord> words;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("game")
    private List<Team> teams;

    //null - not started, true - started, false - finished
    private Boolean isActive;

    @ManyToOne
    @JsonIgnoreProperties("lastGame")
    private Player owner;

    @Enumerated(EnumType.STRING)
    private GameProgress gameProgress;

    //TODO: should be @OneToMany
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "game_watcher",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id"))
    private Set<Player> watchers;

    @OneToOne
    private Team teamTurn;
}
