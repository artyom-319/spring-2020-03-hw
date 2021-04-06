package com.etn319.service.common.impl;

import com.etn319.dao.mongo.CommentMongoRepository;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import com.etn319.security.acl.CreateAcl;
import com.etn319.security.acl.DeleteAcl;
import com.etn319.security.acl.ObjectOrId;
import com.etn319.service.EntityDoesNotExistException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.common.EmptyMandatoryFieldException;
import com.etn319.service.common.api.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.etn319.security.Roles.ROLE_CAN_COMMENT;
import static com.etn319.security.Roles.ROLE_CAN_DELETE;

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
        Objects.requireNonNull(comment.getCommenter(), "Commenter name cannot be empty");
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
    @Secured(ROLE_CAN_COMMENT)
    @CreateAcl
    public Comment create(Comment comment) {
        Objects.requireNonNull(comment);
        if (comment.getId() != null)
            throw new ServiceLayerException("Id of new comment must be null");
        return save(comment);
    }

    @Override
    @PreAuthorize("hasPermission(#comment, 'WRITE')")
    public Comment update(Comment comment) {
        Objects.requireNonNull(comment);
        if (comment.getId() == null)
            throw new ServiceLayerException("Id of comment to be updated must not be null");
        return save(comment);
    }

    @Override
    @DeleteAcl(byObjectId = true, aclClass = Comment.class)
    @PreAuthorize("hasPermission(#id, 'com.etn319.model.Comment', 'DELETE')")
    public void deleteById(@ObjectOrId String id) {
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
