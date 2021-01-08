package com.etn319.web;

import com.etn319.service.common.EmptyMandatoryFieldException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class EmptyMandatoryFieldControllerAdvice {
    @ExceptionHandler(EmptyMandatoryFieldException.class)
    public String handle(Model model, EmptyMandatoryFieldException e) {
        log.info("Empty Mandatory field", e);
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        model.addAttribute("message", e.getMessage());
        return "error";
    }
}
