package com.etn319.security.acl;

import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class IdentityExtractorRegistry {
    private Map<Class<?>, Method> extractors = new HashMap<>();

    public void registerExtractor(Class<?> klass, String idProviderMethodName) {
        Method method = ReflectionUtils.findMethod(klass, idProviderMethodName);
        if (method == null) {
            throw new RuntimeException("Method providing id not found");
        }
        for (Class<?> i : method.getReturnType().getInterfaces()) {
            if (i.equals(Serializable.class)) {
                extractors.put(klass, method);
                return;
            }
        }
        throw new RuntimeException("Method must but does not return Serializable");
    }

    public Method getForClass(Class<?> klass) {
        return extractors.get(klass);
    }
}
