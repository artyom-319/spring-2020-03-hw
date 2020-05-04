package com.etn319.resource;

import java.io.InputStream;

public interface ResourceResolver {
    InputStream getResourceAsStream(String fileName);
}
