package com.etn319.domain;

public class AnswerException extends RuntimeException {
    private int code;
    private Object[] args;

    public AnswerException(int code) {
        super();
        this.code = code;
    }

    public AnswerException(int code, Object ...args) {
        this(code);
        this.args = args;
    }

    public int getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }
}
