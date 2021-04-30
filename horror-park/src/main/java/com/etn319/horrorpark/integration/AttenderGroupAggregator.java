package com.etn319.horrorpark.integration;

import com.etn319.horrorpark.domain.Attender;
import com.etn319.horrorpark.domain.AttenderGroup;
import org.springframework.integration.annotation.Aggregator;

import java.util.Collection;
import java.util.HashSet;

public class AttenderGroupAggregator {
    @Aggregator
    public AttenderGroup aggregator(Collection<Attender> attenders) {
        AttenderGroup attenderGroup = new AttenderGroup();
        attenderGroup.setAttenders(new HashSet<>(attenders));
        return attenderGroup;
    }
}
