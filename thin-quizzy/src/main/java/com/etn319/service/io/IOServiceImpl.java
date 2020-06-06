package com.etn319.service.io;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.PrintStream;
import java.util.Scanner;

@Service
public class IOServiceImpl implements IOService {
    private final IOContext context;
    private PrintStream out;
    private Scanner in;

    public IOServiceImpl(IOContext context) {
        this.context = context;
    }

    @Override
    public void print(String message) {
        getOut().println(message);
    }

    @Override
    public String read() {
        return scanTillNotEmpty(getIn());
    }

    private PrintStream getOut() {
        if (out == null)
            out = context.getOut();
        return out;
    }

    private Scanner getIn() {
        if (in == null)
            in = new Scanner(context.getIn());
        return in;
    }

    private String scanTillNotEmpty(Scanner scanner) {
        String result = scanner.nextLine();

        while (StringUtils.isEmpty(result))
            result = scanner.nextLine();

        return result;
    }
}
