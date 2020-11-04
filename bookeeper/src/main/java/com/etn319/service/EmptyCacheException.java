package com.etn319.service;

import lombok.Getter;

@Getter
public class EmptyCacheException extends ServiceLayerException {
    private final String missedEntity;

    public EmptyCacheException(String missedEntity) {
        super(missedEntity);
        this.missedEntity = missedEntity;
    }
}
