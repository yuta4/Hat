package com.yuta4.hat.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Data
@ToString(of = {"id", "players", "playerTurn", "score"})
@EqualsAndHashCode(of = {"game", "players"})
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne
    @JsonIgnoreProperties("teams")
    private Game game;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id"))
    @OrderBy("login")
    private Set<Player> players = new LinkedHashSet<>();

    @ManyToOne
    private Player playerTurn;

    private Short score = 0;
}
