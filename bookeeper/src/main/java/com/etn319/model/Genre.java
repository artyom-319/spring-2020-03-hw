package com.etn319.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    private long id;
    private String title;

    public Genre(String title) {
        this.title = title;
    }
}
