package com.yuta4.hat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = {"name", "login"})
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_generator")
    @SequenceGenerator(name="player_generator", sequenceName = "player_seq")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @ManyToOne
    @JsonIgnore
    @JsonIgnoreProperties("owner")
    private Game lastGame;

    @Column(nullable = false, unique = true)
    private String login;

}
