package com.etn319.service.common.impl;

import com.etn319.dao.mongo.CommentMongoRepository;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.service.EntityDoesNotExistException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.common.EmptyMandatoryFieldException;
import com.etn319.service.common.api.CommentService;
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
public class CommentServiceImpl implements CommentService {
    private final CommentMongoRepository dao;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public boolean exists(String id) {
        return dao.existsById(id);
    }

    @Override
    public Optional<Comment> getById(String id) {
        return dao.findById(id);
    }

    @Override
    public Optional<Comment> first() {
        return dao.findOne(Example.of(new Comment()));
    }

    @Override
    public List<Comment> getAll() {
        return dao.findAll();
    }

    @Override
    public Comment save(Comment comment) {
        Objects.requireNonNull(comment);
        checkNotEmpty(comment.getCommenter(), "Commenter name cannot be empty");
        checkNotEmpty(comment.getText(), "Comment text cannot be empty");
        if (comment.getBook() == null) {
            throw new EmptyMandatoryFieldException("A book must be wired in order to save comment");
        }

        try {
            return dao.save(comment);
        } catch (DataAccessException e) {
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public void deleteById(String id) {
        if (!dao.existsById(id)) {
            throw new EntityDoesNotExistException("Could not delete: comment id=" + id + " does not exist");
        }

        try {
            dao.deleteById(id);
        } catch (DataAccessException e) {
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public List<Comment> getByBook(Book book) {
        return dao.findAllByBook(book);
    }

    @Override
    public List<Comment> getByCommenterName(String name) {
        return dao.findAllByCommenter(name);
    }

    private void checkNotEmpty(String source, String message) {
        if (source == null || source.trim().isBlank())
            throw new EmptyMandatoryFieldException(message);
    }
}
