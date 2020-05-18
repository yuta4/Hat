package com.yuta4.hat.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(of = {"game", "word"})
@JsonIgnoreProperties({"game"})
public class GameWord {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_word_generator")
    @SequenceGenerator(name="game_word_generator", sequenceName = "game_word_seq")
    private Long id;

    @ManyToOne
    private Game game;

    @ManyToOne
    private Word word;

    @ManyToOne
    private Team team;

    private Boolean currentTurnGuessed;
}
