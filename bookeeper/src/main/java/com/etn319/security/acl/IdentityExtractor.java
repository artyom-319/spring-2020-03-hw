package com.etn319.security.acl;

import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

public class IdentityExtractor {
    private final IdentityExtractorRegistry registry;

    public IdentityExtractor(IdentityExtractorRegistry registry) {
        this.registry = registry;
    }

    public Serializable extractId(Object entity) {
        Method extractor = registry.getForClass(entity.getClass());
        if (extractor == null)
            throw new RuntimeException("Id extractor not found for type " + entity.getClass());
        Object extracted = ReflectionUtils.invokeMethod(extractor, entity);
        if (extracted instanceof Serializable)
            return (Serializable) extracted;
        throw new RuntimeException("Extracted identity must be but is not Serializable");
    }
}
