package com.github.hdghg.rbmon2.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Transition {

    private String name;
    private boolean alive;
    private Timestamp at;

}
