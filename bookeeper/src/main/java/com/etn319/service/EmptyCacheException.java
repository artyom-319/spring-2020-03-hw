package com.etn319.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmptyCacheException extends RuntimeException {
    private final String missedEntity;
}
