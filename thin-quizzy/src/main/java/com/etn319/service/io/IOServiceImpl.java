package com.etn319.service.io;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.PrintStream;
import java.util.Scanner;

@Service
public class IOServiceImpl implements IOService {
    private final PrintStream out;
    private final Scanner in;

    public IOServiceImpl() {
        this.out = System.out;
        this.in = new Scanner(System.in);
    }

    @Override
    public void print(String message) {
        out.println(message);
    }

    @Override
    public String read() {
        return scanTillNotEmpty(in);
    }

    private String scanTillNotEmpty(Scanner scanner) {
        String result = scanner.nextLine();

        while (StringUtils.isEmpty(result))
            result = scanner.nextLine();

        return result;
    }
}
