package com.yuta4.hat.entities;

import com.yuta4.hat.Language;
import com.yuta4.hat.Level;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(of = {"string", "language", "level"})
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_generator")
    @SequenceGenerator(name="word_generator", sequenceName = "word_seq")
    private Long id;

    @Column(nullable = false)
    private String string;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;

    @Column(nullable = false)
    private Integer used = 0;

}
