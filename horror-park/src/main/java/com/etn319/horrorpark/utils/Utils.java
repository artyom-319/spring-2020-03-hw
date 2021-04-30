package com.etn319.horrorpark.utils;

import lombok.SneakyThrows;
import org.slf4j.Logger;

public class Utils {
    public static void logAndWait(Logger log, String message, Object... options) {
        logAndWait(log, 1000, message, options);
    }

    @SneakyThrows
    public static void logAndWait(Logger log, int waitSeconds, String message, Object... options) {
        log.info(message, options);
        Thread.sleep(waitSeconds);
    }
}
