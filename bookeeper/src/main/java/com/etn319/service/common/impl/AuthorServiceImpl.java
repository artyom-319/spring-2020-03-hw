package com.etn319.service.common.impl;

import com.etn319.dao.mongo.AuthorMongoRepository;
import com.etn319.model.Author;
import com.etn319.service.EntityNotFoundException;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.common.EmptyMandatoryFieldException;
import com.etn319.service.common.api.AuthorService;
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
public class AuthorServiceImpl implements AuthorService {
    private final AuthorMongoRepository dao;

    @Override
    public long count() {
        return dao.count();
    }

    @Override
    public Optional<Author> getById(String id) {
        return dao.findById(id);
    }

    @Override
    public Optional<Author> getByName(String name) {
        return dao.findByName(name);
    }

    @Override
    public Optional<Author> first() {
        return dao.findOne(Example.of(new Author()));
    }

    @Override
    public List<Author> getAll() {
        return dao.findAll();
    }

    @Override
    public Author save(Author author) {
        Objects.requireNonNull(author);
        checkNotEmpty(author.getName(), "Author name cannot be empty");
        try {
            return dao.save(author);
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

    @SuppressWarnings("SameParameterValue")
    private void checkNotEmpty(String source, String message) {
        if (source == null || source.trim().isBlank())
            throw new EmptyMandatoryFieldException(message);
    }
}
