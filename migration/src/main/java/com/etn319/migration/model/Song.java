package com.etn319.migration.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Song {
    private String title;
    private Album album;
    private long length;
}
