package com.etn319.horrorpark.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

public class AttenderGroup {
    @Getter
    @Setter
    private Set<Attender> attenders = new HashSet<>();

    public AttenderGroup() {}

    public AttenderGroup(Set<Attender> attenders) {
        this.attenders = attenders;
    }

    @Override
    public String toString() {
        return "AttenderGroup{" +
                "size=" + attenders.size() +
                ", attenders=" + attenders +
                '}';
    }
}
