package com.etn319.service.caching.impl;

import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.model.ServiceUser;
import com.etn319.service.caching.CacheHolder;
import com.etn319.service.caching.api.CommentCachingService;
import com.etn319.service.common.api.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentCachingServiceImpl implements CommentCachingService {
    private final CommentService baseService;
    private final CacheHolder cache;

    @Override
    public long count() {
        return baseService.count();
    }

    @Override
    public boolean exists(String id) {
        return baseService.exists(id);
    }

    @Override
    public Optional<Comment> getById(String id) {
        Optional<Comment> comment = baseService.getById(id);
        comment.ifPresent(cache::setComment);
        return comment;
    }

    @Override
    public Optional<Comment> first() {
        Optional<Comment> comment = baseService.first();
        comment.ifPresent(cache::setComment);
        return comment;
    }

    @Override
    public List<Comment> getAll() {
        return baseService.getAll();
    }

    @Override
    public Comment save() {
        var comment = cache.getComment();
        return baseService.save(comment);
    }

    @Override
    public Comment save(Comment comment) {
        return baseService.save(comment);
    }

    @Override
    public void deleteById(String id) {
        baseService.deleteById(id);
    }

    @Override
    public List<Comment> getByBook() {
        var cachedBook = cache.getBook();
        return baseService.getByBook(cachedBook);
    }

    @Override
    public List<Comment> getByBook(Book book) {
        return baseService.getByBook(book);
    }

    @Override
    public List<Comment> getByCommenterName(String name) {
        return baseService.getByCommenterName(name);
    }

    @Override
    public Comment create(String text, String commenter) {
        var comment = new Comment(text, createUserByName(commenter), null);
        cache.setComment(comment);
        return comment;
    }

    @Override
    public Comment change(String text, String commenter) {
        var comment = cache.getComment();
        if (text != null)
            comment.setText(text);
        if (commenter != null)
            comment.setCommenter(createUserByName(commenter));
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

    private ServiceUser createUserByName(String name) {
        var user = new ServiceUser();
        user.setName(name);
        return user;
    }
}
