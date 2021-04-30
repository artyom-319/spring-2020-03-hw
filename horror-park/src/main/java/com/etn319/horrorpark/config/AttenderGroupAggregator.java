package com.etn319.horrorpark.config;

import com.etn319.horrorpark.domain.Attender;
import com.etn319.horrorpark.domain.AttenderGroup;
import com.etn319.horrorpark.misc.AttenderGroupCoordinator;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.ReleaseStrategy;
import org.springframework.messaging.Message;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class AttenderGroupAggregator {
    private AttenderGroupCoordinator groupCoordinator;

    public AttenderGroupAggregator(AttenderGroupCoordinator groupCoordinator) {
        this.groupCoordinator = groupCoordinator;
    }

    @Aggregator
    public AttenderGroup aggregator(Collection<Attender> attenders) {
        AttenderGroup attenderGroup = new AttenderGroup();
        attenderGroup.setAttenders(new HashSet<>(attenders));
        return attenderGroup;
    }

//    @ReleaseStrategy
//    public boolean canRelease(List<Message<?>> messages) {
//        return messages.size() >= groupCoordinator.aliveCount();
//    }
}
