package com.etn319.migration.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Album {
    private String title;
    private List<Song> compositions;
    private int releaseYear;
}
