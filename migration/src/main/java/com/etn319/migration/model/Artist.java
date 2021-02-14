package com.etn319.migration.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Artist {
    private String name;
    private int birthYear;
    private List<Song> topSongs;
}
