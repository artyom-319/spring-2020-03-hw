package com.etn319.horrorpark.stages;

import com.etn319.horrorpark.domain.Attender;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

import static com.etn319.horrorpark.utils.Utils.logAndWait;

@Slf4j
public class MineField {
    private final double mineChance;
    private final int length;
    private final int minDamage;
    private final int maxDamage;

    public MineField(int length, double mineChance, int minDamage, int maxDamage) {
        this.length = length;
        this.mineChance = mineChance;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }

    public Attender accept(Attender attender) {
        // todo: remove
        if (attender.isAlive()) {
            logAndWait(log, "{} попадает на минное поле. Вряд ли он был к этому готов... Что ж, удачи!",
                    attender.getName());
            for (int step = 0; step < length; step++) {
                tryMakeStep(attender);
                if (!attender.isAlive()) {
                    logAndWait(log, "{} погибает при попытке преодолеть минное поле. Увы :(", attender.getName());
                    return attender;
                }
            }
            logAndWait(log, "{} преодолевает минное поле!", attender.getName());
        } else {
            log.error("поступил мертвец, где катафалки??");
        }
        return attender;
    }

    @SneakyThrows
    private void tryMakeStep(Attender attender) {
        boolean mineIsHere = new Random().nextDouble() < mineChance;
        if (mineIsHere) {
            int damage = new Random().nextInt(maxDamage - minDamage + 1) + minDamage;
            attender.getHealth().dec(damage);
            logAndWait(log, "{} наступает на мину и получает {} урона! Остаётся {} здоровья",
                    attender.getName(), damage, attender.getHealth().getValue());
            tryMakeStep(attender);
        }
    }
}
