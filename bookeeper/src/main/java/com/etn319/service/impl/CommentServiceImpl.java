package com.etn319.service.impl;

import com.etn319.dao.DaoLayerException;
import com.etn319.dao.api.CommentDao;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.service.CacheHolder;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.api.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentDao dao;
    private final CacheHolder cache;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public Optional<Comment> getById(long id) {
        Optional<Comment> comment = dao.getById(id);
        comment.ifPresent(cache::setComment);
        return comment;
    }

    @Override
    public List<Comment> getAll() {
        return dao.getAll();
    }

    @Override
    public Comment save() {
        var comment = cache.getComment();

        try {
            Comment saved = dao.save(comment);
            clearCache();
            return saved;
        } catch (DaoLayerException e) {
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        try {
            dao.deleteById(id);
        } catch (DaoLayerException e) {
            throw new ServiceLayerException(e);
        }
    }

    @Override
    public List<Comment> getByBook() {
        return dao.getByBook(cache.getBook());
    }

    @Override
    public List<Comment> getByCommenterName(String name) {
        return dao.getByCommenterName(name);
    }

    @Override
    public Comment create(String text, String commenter) {
        var comment = new Comment();
        comment.setText(text);
        comment.setCommenter(commenter);
        comment.setBook(cache.getBook());
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
    public void clearCache() {
        cache.clearComment();
    }

    @Override
    public Comment getCache() {
        return cache.getComment();
    }
}
