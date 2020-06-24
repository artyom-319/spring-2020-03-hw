package com.etn319.dao;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DaoLayerException extends RuntimeException {
    public DaoLayerException(Throwable cause) {
        super(cause);
    }
}
