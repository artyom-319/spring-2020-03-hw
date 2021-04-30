package com.etn319.horrorpark.misc;

import com.etn319.horrorpark.domain.Attender;
import com.etn319.horrorpark.domain.AttenderGroup;
import lombok.Getter;
import lombok.Setter;

public class AttenderGroupCoordinator {
    @Getter
    @Setter
    private AttenderGroup initialGroup;
    
    public int aliveCount() {
        return (int) initialGroup.getAttenders()
                .stream()
                .filter(Attender::isAlive)
                .count();
    }
}
