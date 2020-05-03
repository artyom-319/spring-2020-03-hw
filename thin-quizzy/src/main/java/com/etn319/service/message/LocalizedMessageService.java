package com.etn319.service.message;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class LocalizedMessageService implements MessageService {
    private final MessageSource messageSource;
    private final Locale locale;

    public LocalizedMessageService(MessageSource messageSource, Locale locale) {
        this.messageSource = messageSource;
        this.locale = locale;
    }

    @Override
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }
}
