package com.github.hdghg.rbmon2.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CharacterBonus {

    private int id;
    private String nickname;
    private Timestamp at;

}
