package com.etn319.dao;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ConnectedEntityDoesNotExistException extends RuntimeException {
    public ConnectedEntityDoesNotExistException(Throwable cause) {
        super(cause);
    }
}
