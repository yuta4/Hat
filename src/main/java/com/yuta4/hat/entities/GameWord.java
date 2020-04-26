package com.yuta4.hat.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class GameWord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Game game;

    @ManyToOne
    private Word word;

    @ManyToOne
    private Team team;
}
