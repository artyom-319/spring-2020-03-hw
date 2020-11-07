package com.etn319.service.impl;

import com.etn319.dao.BookRepository;
import com.etn319.dao.CommentRepository;
import com.etn319.model.Comment;
import com.etn319.service.CacheHolder;
import com.etn319.service.EntityNotFoundException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.api.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository dao;
    private final BookRepository bookDao;
    private final CacheHolder cache;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public Optional<Comment> getById(long id) {
        Optional<Comment> comment = dao.findById(id);
        comment.ifPresent(cache::setComment);
        return comment;
    }

    @Override
    public List<Comment> getAll() {
        return dao.findAll();
    }

    @Override
    @Transactional
    public Comment save() {
        var comment = cache.getComment();

        try {
            Comment saved = dao.save(comment);
            clearCache();
            return saved;
        } catch (DataAccessException e) {
            throw new ServiceLayerException(e);
        }
    }

    @Override
    @Transactional
    public void deleteById(long id) {
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
    @Transactional(readOnly = true)
    public List<Comment> getByBook() {
        var cachedBook = cache.getBook();
        var foundBook = bookDao.findById(cachedBook.getId())
                .orElseThrow(() -> new ServiceLayerException("Book does not exist"));
        return new ArrayList<>(foundBook.getComments());
    }

    @Override
    public List<Comment> getByCommenterName(String name) {
        return dao.findByCommenter(name);
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
