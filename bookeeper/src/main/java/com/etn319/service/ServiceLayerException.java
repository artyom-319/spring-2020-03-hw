package com.etn319.service;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServiceLayerException extends RuntimeException {
    public ServiceLayerException(String message) {
        super(message);
    }

    public ServiceLayerException(Throwable cause) {
        super(cause);
    }
}
