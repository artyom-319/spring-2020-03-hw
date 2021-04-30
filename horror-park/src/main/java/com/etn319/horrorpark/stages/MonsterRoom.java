package com.etn319.horrorpark.stages;

import com.etn319.horrorpark.domain.Attender;
import com.etn319.horrorpark.domain.AttenderGroup;
import com.etn319.horrorpark.stages.stageprops.Monster;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.etn319.horrorpark.utils.Utils.logAndWait;

@Slf4j
public class MonsterRoom {
    private final Monster monster;

    public MonsterRoom(Monster monster) {
        this.monster = monster;
    }

    public AttenderGroup accept(AttenderGroup attenderGroup) {
        Set<Attender> attenders = attenderGroup.getAttenders();
        logAndWait(log, "Группа из посетителей {} входит в комнату Чудовища...", attenders);
        Map<Boolean, List<Attender>> escapedOrStaying = attenders.stream()
                .collect(Collectors.partitioningBy(attender -> attender.getAgility().getValue() >= monster.getAgility()));
        List<Attender> escaping = escapedOrStaying.get(true);
        List<Attender> staying = escapedOrStaying.get(false);
        logAndWait(log, "Особо ловкие сбежали от Чудовища в сторону следующей комнаты! Это {}", escaping);
        monster.react(staying, escaping);
        logAndWait(log, "Группа покидает комнату Чудовища");
        return attenderGroup;
    }
}
