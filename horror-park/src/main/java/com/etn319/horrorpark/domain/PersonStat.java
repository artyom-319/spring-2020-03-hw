package com.etn319.horrorpark.domain;

import lombok.Getter;

import java.util.Random;

public class PersonStat {
    public static final int MIN_INITIAL_VALUE = 5;
    public static final int MAX_INITIAL_VALUE = 10;
    public static PersonStat ZERO_VALUE = new PersonStat(0);

    @Getter
    private int value;

    PersonStat(int initialValue) {
        value = initialValue;
    }

    public void inc() {
        value++;
    }

    public void dec() {
        if (value > 0)
            value--;
    }

    public void inc(int n) {
        value += n;
    }

    public void dec(int n) {
        if (value - n < 0)
            value = 0;
        else
            value -= n;
    }

    public void obnulate() {
        value = 0;
    }

    public static PersonStat assign() {
        return new PersonStat(new Random()
                .nextInt(MAX_INITIAL_VALUE - MIN_INITIAL_VALUE) + MIN_INITIAL_VALUE);
    }

    @Override
    public String toString() {
        return "PersonStat{" +
                "value=" + value +
                '}';
    }
}
