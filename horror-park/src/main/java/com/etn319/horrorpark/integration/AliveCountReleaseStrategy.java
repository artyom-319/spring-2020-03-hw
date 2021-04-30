package com.etn319.horrorpark.integration;

import com.etn319.horrorpark.misc.AttenderGroupCoordinator;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.store.MessageGroup;

public class AliveCountReleaseStrategy implements ReleaseStrategy {
    private final AttenderGroupCoordinator groupCoordinator;

    public AliveCountReleaseStrategy(AttenderGroupCoordinator groupCoordinator) {
        this.groupCoordinator = groupCoordinator;
    }

    @Override
    public boolean canRelease(MessageGroup group) {
        return group.size() >= groupCoordinator.aliveCount();
    }
}
