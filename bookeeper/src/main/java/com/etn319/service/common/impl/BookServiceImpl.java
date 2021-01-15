package com.etn319.service.common.impl;

import com.etn319.dao.mongo.BookMongoRepository;
import com.etn319.dao.mongo.CommentMongoRepository;
import com.etn319.model.Author;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.model.Genre;
import com.etn319.service.EntityNotFoundException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.common.EmptyMandatoryFieldException;
import com.etn319.service.common.api.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Primary
public class BookServiceImpl implements BookService {
    private final BookMongoRepository dao;
    private final CommentMongoRepository commentDao;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public boolean exists(String id) {
        return dao.existsById(id);
    }

    @Override
    public Optional<Book> getById(String id) {
        Optional<Book> oBook = dao.findById(id);
        if (oBook.isPresent()) {
            Book book = oBook.get();
            List<Comment> comments = commentDao.findAllByBook(book);
            book.setComments(comments);
        }
        return oBook;
    }

    @Override
    public Optional<Book> first() {
        return dao.findOne(Example.of(new Book()));
    }

    @Override
    public List<Book> getAll() {
        return dao.findAll();
    }

    @Override
    public Book save(Book book) {
        Objects.requireNonNull(book);
        checkNotEmpty(book.getTitle(), "Book title cannot be empty");
        try {
            // вопрос: насколько корректно так делать, чтобы с фронта не гонять всю информацию о
            // о вложенных сущностях, а в ответ вкладывать полную информацию
            var saved = dao.save(book);
            Optional<Book> found = dao.findById(saved.getId());
            return found.orElseThrow();
        } catch (DataAccessException e) {
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public void deleteById(String id) {
        if (!dao.existsById(id)) {
            throw new EntityNotFoundException();
        }

        try {
            dao.deleteById(id);
        } catch (DataAccessException e) {
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public List<Book> getByGenre(Genre genre) {
        return getByGenreTitle(genre.getTitle());
    }

    @Override
    public List<Book> getByGenreTitle(String title) {
        return dao.findAllByGenreTitle(title);
    }

    @Override
    public List<Book> getByAuthor(Author author) {
        return getByAuthorId(author.getId());
    }

    @Override
    public List<Book> getByAuthorId(String id) {
        return dao.findAllByAuthor_id(id);
    }

    @SuppressWarnings("SameParameterValue")
    private void checkNotEmpty(String source, String message) {
        if (source == null || source.trim().isBlank())
            throw new EmptyMandatoryFieldException(message);
    }
}
