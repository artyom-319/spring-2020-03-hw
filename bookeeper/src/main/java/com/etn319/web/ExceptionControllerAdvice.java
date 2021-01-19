package com.etn319.web;

import com.etn319.service.EntityDoesNotExistException;
import com.etn319.service.common.EmptyMandatoryFieldException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(EmptyMandatoryFieldException.class)
    public ResponseEntity<String> handleEmptyMandatoryField(EmptyMandatoryFieldException e) {
        log.info("Empty Mandatory field", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(Exception e) {
        log.info("Not found", e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(EntityDoesNotExistException.class)
    public ResponseEntity<String> handleNotExists(Exception e) {
        log.info("Not exists", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
