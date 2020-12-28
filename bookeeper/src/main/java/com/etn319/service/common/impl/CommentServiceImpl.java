package com.etn319.service.common.impl;

import com.etn319.dao.mongo.CommentMongoRepository;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.service.EntityNotFoundException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.common.api.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
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
        // проверять на пустоту комментария и автора
        if (comment.getBook() == null) {
            throw new ServiceLayerException("Failed to save comment because it has no book wired");
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
            throw new EntityNotFoundException();
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
}
