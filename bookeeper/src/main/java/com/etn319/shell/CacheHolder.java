package com.etn319.shell;

import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Genre;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CacheHolder {
    private Author author;
    private Genre genre;
    private Book book;
}
