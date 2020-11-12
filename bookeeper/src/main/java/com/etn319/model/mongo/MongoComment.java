package com.etn319.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoComment {

    @Id
    private String id;

    @Field("commenter")
    private String commenter;

    @Field("text")
    private String text;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", commenter='" + commenter + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
