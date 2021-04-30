package com.etn319.horrorpark.domain;

public class DeadAttender extends Attender {
    public DeadAttender(Attender attender) {
        super(attender.getName());
    }

    @Override
    public PersonStat getHealth() {
        return PersonStat.ZERO_VALUE;
    }

    @Override
    public PersonStat getAgility() {
        return PersonStat.ZERO_VALUE;
    }

    @Override
    public PersonStat getStrength() {
        return PersonStat.ZERO_VALUE;
    }

    @Override
    public PersonStat getIntellect() {
        return PersonStat.ZERO_VALUE;
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public boolean isDead() {
        return true;
    }

    @Override
    public String toString() {
        return "DeadAttender{" +
                "name='" + getName() + '\'' +
                '}';
    }
}
