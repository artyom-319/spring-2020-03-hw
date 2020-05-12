package com.etn319.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    private long id;
    private String name;
    private String country;

    public Author(String name, String country) {
        this.name = name;
        this.country = country;
    }
}