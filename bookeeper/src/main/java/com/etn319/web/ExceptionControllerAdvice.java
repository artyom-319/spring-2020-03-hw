package com.etn319.web;

import com.etn319.service.EntityDoesNotExistException;
import com.etn319.service.common.EmptyMandatoryFieldException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(EmptyMandatoryFieldException.class)
    public Mono<ResponseEntity<String>> handleEmptyMandatoryField(EmptyMandatoryFieldException e) {
        log.info("Empty Mandatory field", e);
        return Mono.just(e.getMessage())
                .map(m -> ResponseEntity.badRequest().body(m));
    }

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<String>> handleNotFound(Exception e) {
        log.info("Not found", e);
        return Mono.just(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(EntityDoesNotExistException.class)
    public Mono<ResponseEntity<String>> handleNotExists(Exception e) {
        log.info("Not exists", e);
        return Mono.just(e.getMessage())
                .map(m -> ResponseEntity.badRequest().body(m));
    }
}
