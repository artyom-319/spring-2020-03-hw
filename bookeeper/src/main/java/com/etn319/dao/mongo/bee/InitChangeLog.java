package com.etn319.dao.mongo.bee;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.model.Genre;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import org.springframework.data.mongodb.core.MongoTemplate;

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

    @ChangeSet(order = "000", id = "dropDB", author = "etn319", runAlways = true)
    public void dropDB(MongoDatabase database){
        database.drop();
    }

    @ChangeSet(order = "001", id = "initAuthors", author = "etn319", runAlways = true)
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

    @ChangeSet(order = "002", id = "initBooks", author = "etn319", runAlways = true)
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

    @ChangeSet(order = "003", id = "initComments", author = "etn319", runAlways = true)
    public void insertComments(MongoTemplate template) {
        template.save(new Comment("10/10, pishi esche", "Dan", eden));
        template.save(new Comment("5/10", "Eugene", comrades));
        template.save(new Comment("Good", "Kate", seaWolf));
        template.save(new Comment("Super", "Kate", eden));
        template.save(new Comment("Very nice", "Albert", comrades));
        template.save(new Comment("So sad", "Albert", othello));
    }
}
