package com.etn319.model.mongo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document("authors")
@NoArgsConstructor
public class MongoAuthor {

    @Id
    private String id;

    @Field("name")
    @Indexed(unique = true)
    private String name;

    @Field("country")
    private String country;

    public MongoAuthor(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public MongoAuthor(String id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
