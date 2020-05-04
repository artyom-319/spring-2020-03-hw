package com.etn319.resource;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class LocalizedResourceResolver implements ResourceResolver {
    private final LocalizedResourceHelper localizedResourceHelper;
    private String extension;
    private Locale locale;

    public LocalizedResourceResolver(LocalizedResourceHelper localizedResourceHelper) {
        this.localizedResourceHelper = localizedResourceHelper;
    }

    @Override
    public InputStream getResourceAsStream(String fileName) {
        Resource resource = localizedResourceHelper.findLocalizedResource(fileName, extension, locale);
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
