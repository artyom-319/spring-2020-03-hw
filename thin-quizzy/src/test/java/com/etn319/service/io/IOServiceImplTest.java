package com.etn319.service.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class IOServiceImplTest {
    private static final String INPUT_STRING = "input";
    private static final String OUTPUT_STRING = "output";

    private OutputStream out;
    private InputStream in;

    @Configuration
    static class Config {
        @Bean
        IOService ioService(IOContext context) {
            return new IOServiceImpl(context);
        }
    }

    @MockBean
    private IOContext context;
    @Autowired
    private IOService ioService;

    @BeforeEach
    public void setUp() {
        in = new ByteArrayInputStream(INPUT_STRING.getBytes());
        out = new ByteArrayOutputStream();
        given(context.getIn()).willReturn(in);
        given(context.getOut()).willReturn(new PrintStream(out));
    }

    @Test
    void print() {
        ioService.print(OUTPUT_STRING);
        assertThat(out.toString()).isEqualTo(OUTPUT_STRING + "\n");
    }

    @Test
    void read() {
        String readString = ioService.read();
        assertThat(readString).isEqualTo(INPUT_STRING);
    }
}
