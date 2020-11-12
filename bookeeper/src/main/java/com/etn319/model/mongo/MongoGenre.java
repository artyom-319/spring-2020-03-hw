package com.etn319.model.mongo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("genres")
@Data
@NoArgsConstructor
public class MongoGenre {

    @Id
    private String id;

    @Field("title")
    private String title;

    public MongoGenre(String title) {
        this.title = title;
    }

    public MongoGenre(String id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
