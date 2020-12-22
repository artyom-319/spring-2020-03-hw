package com.etn319.service.impl;

import com.etn319.dao.mongo.CommentMongoRepository;
import com.etn319.model.Comment;
import com.etn319.service.CacheHolder;
import com.etn319.service.EntityNotFoundException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.api.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentMongoRepository dao;
    private final CacheHolder cache;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public Optional<Comment> getById(String id) {
        Optional<Comment> comment = dao.findById(id);
        comment.ifPresent(cache::setComment);
        return comment;
    }

    @Override
    public List<Comment> getAll() {
        return dao.findAll();
    }

    @Override
    public Comment save() {
        var comment = cache.getComment();
        if (comment.getBook() == null) {
            throw new ServiceLayerException("Failed to save comment because it has no book wired");
        }

        try {
            Comment saved = dao.save(comment);
            clearCache();
            return saved;
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
    public List<Comment> getByBook() {
        var cachedBook = cache.getBook();
        return dao.findAllByBook(cachedBook);
    }

    @Override
    public List<Comment> getByCommenterName(String name) {
        return dao.findAllByCommenter(name);
    }

    @Override
    public Comment create(String text, String commenter) {
        var comment = new Comment();
        comment.setText(text);
        comment.setCommenter(commenter);
        cache.setComment(comment);
        return comment;
    }

    @Override
    public Comment change(String text, String commenter) {
        var comment = cache.getComment();
        if (text != null)
            comment.setText(text);
        if (commenter != null)
            comment.setCommenter(commenter);
        return comment;
    }

    @Override
    public Comment wireBook() {
        var comment = getCache();
        var book = cache.getBook();
        comment.setBook(book);
        return comment;
    }

    @Override
    public void clearCache() {
        cache.clearComment();
    }

    @Override
    public Comment getCache() {
        return cache.getComment();
    }
}
