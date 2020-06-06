package com.etn319.dao;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }
}
