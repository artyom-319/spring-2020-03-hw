package com.etn319.service;

import java.io.InputStream;

public interface ResourceResolver {
    InputStream getResourceAsStream(String fileName);
}
