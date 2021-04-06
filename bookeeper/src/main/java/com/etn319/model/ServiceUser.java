package com.etn319.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceUser {
    @Id
    private String id;

    @Field("name")
    @Indexed(unique = true)
    private String name;

    @Field("pass")
    private String pass;

    @Field("authorities")
    private List<String> authorities;

    public ServiceUser(String name, String pass, List<String> authorities) {
        this.name = name;
        this.pass = pass;
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
