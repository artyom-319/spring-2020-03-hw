package com.etn319.horrorpark.domain;

import lombok.Getter;
import lombok.Setter;

public class Attender {
    @Getter
    private final String name;
    @Getter @Setter
    private PersonStat health;
    @Getter @Setter
    private PersonStat agility;
    @Getter @Setter
    private PersonStat strength;
    @Getter @Setter
    private PersonStat intellect;

    public Attender(String name) {
        this.name = name;
        this.health = PersonStat.assign();
        this.agility = PersonStat.assign();
        this.strength = PersonStat.assign();
        this.intellect = PersonStat.assign();
    }

    public boolean isAlive() {
        return health.getValue() > 0;
    }

    public boolean isDead() {
        return !isAlive();
    }

    public void injure(int damage) {
        health.dec(damage);
    }

    public void kill() {
        health.obnulate();
    }

    @Override
    public String toString() {
        return "Attender{" +
                "name='" + name + '\'' +
                '}';
    }

    public String toStringWithHealth() {
        return "Attender{" +
                "name='" + name + '\'' +
                ", health=" + health +
                '}';
    }

    public String toDetailedString() {
        return "Attender{" +
                "name='" + name + '\'' +
                ", health=" + health +
                ", agility=" + agility +
                ", strength=" + strength +
                ", intellect=" + intellect +
                '}';
    }
}
