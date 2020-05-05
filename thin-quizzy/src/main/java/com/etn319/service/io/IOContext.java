package com.etn319.service.io;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.PrintStream;

@Component
public class IOContext {
    private PrintStream out = System.out;
    private InputStream in = System.in;

    public IOContext() {}

    public IOContext(PrintStream out, InputStream in) {
        this.out = out;
        this.in = in;
    }

    public PrintStream getOut() {
        return out;
    }

    public InputStream getIn() {
        return in;
    }
}
