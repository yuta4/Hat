package com.yuta4.hat.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuta4.hat.GameProgress;
import com.yuta4.hat.Language;
import com.yuta4.hat.Level;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Data
@ToString(of = {"id", "owner", "isActive", "gameProgress", "teams", "watchers", "teamTurn"})
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<GameWord> words = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("game")
    @OrderBy("id desc")
    private Set<Team> teams = new LinkedHashSet<>();

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
    @OrderBy("login")
    private Set<Player> watchers = new LinkedHashSet<>();

    @OneToOne
    private Team teamTurn;

    private Integer wordsPerPlayer = 15;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Language.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "game_words_language")
    @Column(name = "words_language")
    private Set<Language> wordsLanguages = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Level.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "game_words_level")
    @Column(name = "words_level")
    private Set<Level> wordsLevels = new HashSet<>();
}
