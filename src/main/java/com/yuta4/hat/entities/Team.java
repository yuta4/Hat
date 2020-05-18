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
@EqualsAndHashCode(of = {"id"})
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_generator")
    @SequenceGenerator(name="team_generator", sequenceName = "team_seq")
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

    private Long score = 0L;
}
