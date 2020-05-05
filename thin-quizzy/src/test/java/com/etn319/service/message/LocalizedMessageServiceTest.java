package com.etn319.service.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@SpringBootTest
class LocalizedMessageServiceTest {
    private static final String JUST_CODE = "code";
    private static final String JUST_MESSAGE = "message";
    private static final Object ARG_1 = "str1";
    private static final Object ARG_2 = "str2";
    private static final Object ARG_3 = "str3";

    @Configuration
    static class Config {
        @Bean
        MessageService messageService(@Qualifier("mock") MessageSource messageSource, Locale locale) {
            return new LocalizedMessageService(messageSource, locale);
        }

        @Bean
        Locale locale() {
            return Locale.getDefault();
        }
    }

    @MockBean(name = "mock")
    MessageSource messageSource;
    @Autowired
    MessageService messageService;
    @Autowired
    Locale locale;

    @BeforeEach
    public void setUp() {
        given(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class)))
                .willReturn(JUST_MESSAGE);
    }

    @Test
    void getMessageWithArgs() {
        String message = messageService.getMessage(JUST_CODE, ARG_1, ARG_2, ARG_3);

        ArgumentCaptor<String> msgCodeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> argsCaptor = ArgumentCaptor.forClass(Object[].class);
        ArgumentCaptor<Locale> localeCaptor = ArgumentCaptor.forClass(Locale.class);

        assertThat(message).isEqualTo(JUST_MESSAGE);
        verify(messageSource, only()).getMessage(msgCodeCaptor.capture(), argsCaptor.capture(), localeCaptor.capture());
        assertThat(msgCodeCaptor.getValue()).isEqualTo(JUST_CODE);
        assertThat(argsCaptor.getValue()).containsExactly(ARG_1, ARG_2, ARG_3);
        assertThat(localeCaptor.getValue()).isEqualTo(locale);
    }

    @Test
    void getMessageWithoutArgs() {
        String message = messageService.getMessage(JUST_CODE);

        ArgumentCaptor<String> msgCodeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> argsCaptor = ArgumentCaptor.forClass(Object[].class);
        ArgumentCaptor<Locale> localeCaptor = ArgumentCaptor.forClass(Locale.class);

        assertThat(message).isEqualTo(JUST_MESSAGE);
        verify(messageSource, only()).getMessage(msgCodeCaptor.capture(), argsCaptor.capture(), localeCaptor.capture());
        assertThat(msgCodeCaptor.getValue()).isEqualTo(JUST_CODE);
        assertThat(argsCaptor.getValue()).isEmpty();
        assertThat(localeCaptor.getValue()).isEqualTo(locale);
    }
}
