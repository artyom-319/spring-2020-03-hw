package com.etn319.service;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EntityDoesNotExistException extends RuntimeException {
    public EntityDoesNotExistException(String message) {
        super(message);
    }
}
