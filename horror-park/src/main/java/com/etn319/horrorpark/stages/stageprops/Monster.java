package com.etn319.horrorpark.stages.stageprops;

import com.etn319.horrorpark.domain.Attender;
import com.etn319.horrorpark.domain.PersonStat;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Random;

import static com.etn319.horrorpark.utils.Utils.logAndWait;

@Slf4j
@Getter
public class Monster {
    private final int agility;
    private final int intellect;
    private final int minDamage;
    private final int maxDamage;

    public Monster(int agility, int intellect, int minDamage, int maxDamage) {
        this.agility = agility;
        this.intellect = intellect;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }

    @SneakyThrows
    public void react(Collection<Attender> stayed, Collection<Attender> escaped) {
        boolean wannaPlay = new Random().nextBoolean() && !escaped.isEmpty();
        if (wannaPlay) {
            logAndWait(log, "Чудовище хочет сразиться в шахматы! Коллективного интеллекта должно хватить");
            logAndWait(log, "Чудовище:");
            logAndWait(log, "- Конечно, я успел заметить, что не очень умные ребята попытались сбежать. " +
                    "Догнать их мне не составит труда");
            logAndWait(log, "- Поэтому договариваемся так: если вы побеждаете, то отпускаю всех, если нет, им непоздоровится");
            logAndWait(log,"Играют {}. Приготовиться!", stayed);
            int totalIntellect = stayed.stream()
                    .map(Attender::getIntellect)
                    .map(PersonStat::getValue)
                    .reduce(0, Integer::sum);
            boolean enoughIntellect = totalIntellect > intellect;
            Thread.sleep(5000);
            if (enoughIntellect) {
                logAndWait(log, "Чудовище обыграно и, пребывая в восхищении, отпускает группу!");
            } else {
                logAndWait(log,"Шах, и королю некуда деться, Чудовище самодовольно потирает лапки, сейчас будет жарко");
                escaped.forEach(this::injure);
            }
        } else {
            logAndWait(log, "Чудовище явно не в настроении и сейчас задаст жару");
            stayed.forEach(this::injure);
        }
    }

    private void injure(Attender attender) {
        int damage = new Random().nextInt(maxDamage - minDamage + 1) + minDamage;
        attender.injure(damage);
        if (!attender.isAlive()) {
            log.info("{} получает {} ед. урона и покидает этот мир!", attender.getName(), damage);
        } else {
            log.info("{} получает {} ед. урона, остаётся {} здоровья",
                    attender.getName(), damage, attender.getHealth().getValue());
        }
    }
}
