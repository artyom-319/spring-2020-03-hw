package com.etn319.dao.mongo.bee;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.model.Genre;
import com.etn319.model.ServiceUser;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Slf4j
@ChangeLog
public class InitChangeLog {
    private Author jLondon;
    private Author eRemarque;
    private Author wShakespeare;
    private Author aAzimov;
    private Book eden;
    private Book comrades;
    private Book seaWolf;
    private Book othello;

    private PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @ChangeSet(order = "000", id = "dropDB", author = "etn319", runAlways = true)
    public void dropDB(MongoDatabase database){
        database.drop();
    }

    @ChangeSet(order = "001", id = "initUsers", author = "etn319", runAlways = true)
    public void insertUsers(MongoTemplate template) {
        template.insert(List.of(
                new ServiceUser("admin", encoder.encode("admin"), List.of("ROLE_ADMIN", "ROLE_MODERATOR")),
                new ServiceUser("user", encoder.encode("user"), List.of("ROLE_USER")),
                new ServiceUser("moderator", encoder.encode("moderator"), List.of("ROLE_MODERATOR")))
        , ServiceUser.class);
        log.info("Users: {}", template.findAll(ServiceUser.class));
    }

    @ChangeSet(order = "002", id = "initAuthors", author = "etn319", runAlways = true)
    public void insertAuthors(MongoTemplate template) {
        jLondon = new Author("Jack London", "USA");
        eRemarque = new Author("Erich Maria Remarque", "Germany");
        wShakespeare = new Author("William Shakespeare", "England");
        aAzimov = new Author("Isaac Asimov", "USA");
        template.save(jLondon);
        template.save(eRemarque);
        template.save(wShakespeare);
        template.save(aAzimov);
    }

    @ChangeSet(order = "003", id = "initBooks", author = "etn319", runAlways = true)
    public void insertBooks(MongoTemplate template) {
        eden = new Book("Martin Eden", jLondon, new Genre("Novel"));
        comrades = new Book("Three Comrades", eRemarque, new Genre("Novel"));
        seaWolf = new Book("Sea Wolf", jLondon, new Genre("Novel"));
        othello = new Book("Othello", wShakespeare, new Genre("Tragedy"));
        Book merchant = new Book("The Merchant of Venice", wShakespeare, new Genre("Comedy"));
        Book eternity = new Book("The End of Eternity", aAzimov, new Genre("Science Fiction"));
        template.save(eden);
        template.save(comrades);
        template.save(seaWolf);
        template.save(merchant);
        template.save(othello);
        template.save(eternity);
    }

    @ChangeSet(order = "004", id = "initComments", author = "etn319", runAlways = true)
    public void insertComments(MongoTemplate template) {
        template.save(new Comment("10/10, pishi esche", "Dan", eden));
        template.save(new Comment("5/10", "Eugene", comrades));
        template.save(new Comment("Good", "Kate", seaWolf));
        template.save(new Comment("Super", "Kate", eden));
        template.save(new Comment("Very nice", "Albert", comrades));
        template.save(new Comment("So sad", "Albert", othello));
    }
}
