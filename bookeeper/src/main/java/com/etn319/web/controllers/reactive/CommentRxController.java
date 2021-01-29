package com.etn319.web.controllers.reactive;

import com.etn319.dao.mongo.reactive.CommentReactiveMongoRepository;
import com.etn319.service.ServiceLayerException;
import com.etn319.service.common.EmptyMandatoryFieldException;
import com.etn319.web.dto.CommentDto;
import com.etn319.web.dto.mappers.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/rx")
@RequiredArgsConstructor
public class CommentRxController {
    private final CommentReactiveMongoRepository repository;

    @PostMapping("/api/books/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CommentDto> newComment(@RequestBody CommentDto commentDto, @PathVariable String id) {
        return Mono.just(commentDto)
                .map(CommentMapper::toDomainObject)
                .filter(c -> isNotNullOrBlank(c.getCommenter()))
                .switchIfEmpty(Mono.error(new EmptyMandatoryFieldException("Commenter name cannot be empty")))
                .filter(c -> isNotNullOrBlank(c.getText()))
                .switchIfEmpty(Mono.error(new EmptyMandatoryFieldException("Commenter name cannot be empty")))
                .filter(c -> c.getBook() != null)
                .switchIfEmpty(
                        Mono.error(new EmptyMandatoryFieldException("A book must be wired in order to save comment")))
                .flatMap(repository::save)
                .onErrorMap(DataAccessException.class, ServiceLayerException::new)
                .map(CommentMapper::toDto);
    }

    private boolean isNotNullOrBlank(String source) {
        return source != null && !source.trim().isBlank();
    }
}
