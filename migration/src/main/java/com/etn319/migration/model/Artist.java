package com.etn319.migration.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.List;

@Getter
@Setter
public class Artist {
    @Id
    private String _id;
    @Transient
    private long id;
    private String name;
    private int birthYear;
    private List<Song> topSongs;

    public Artist(long id, String name, int birthYear, List<Song> topSongs) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
        this.topSongs = topSongs;
    }

    public Artist(String name, int birthYear, List<Song> topSongs) {
        this.name = name;
        this.birthYear = birthYear;
        this.topSongs = topSongs;
    }

    public Artist(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "_id='" + _id + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", birthYear=" + birthYear +
                '}';
    }
}
