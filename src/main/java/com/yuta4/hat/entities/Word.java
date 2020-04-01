package com.yuta4.hat.entities;

import com.yuta4.hat.Level;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Word {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String string;

    @Enumerated(EnumType.STRING)

    private Level level;

    private Integer used;

}
