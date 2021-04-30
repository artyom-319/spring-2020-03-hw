package com.etn319.horrorpark.stages;

import com.etn319.horrorpark.domain.Attender;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

import static com.etn319.horrorpark.utils.Utils.logAndWait;

@Slf4j
public class Canyon {
    private int bridgeWidth;
    private int luckyFactor;
    private int windSpeed;

    public Canyon(int bridgeWidth, int windSpeed, int luckyFactor) {
        this.bridgeWidth = bridgeWidth;
        this.luckyFactor = luckyFactor;
        this.windSpeed = windSpeed;
    }

    public Attender accept(Attender attender) {
        logAndWait(log, "{} подходит к каньону. Через него прокинут подвесной мост," +
                " но удержаться на мосту не так-то просто", attender.getName());
        int agility = attender.getAgility().getValue();
        int resultLuckyFactor = new Random().nextInt(2 * luckyFactor) - luckyFactor;
        int sum = agility + resultLuckyFactor + bridgeWidth - windSpeed;
        boolean shallPass = sum > 0;
        if (!shallPass) {
            logAndWait(log, "{} срывается в пропасть, его крик уносится в пустоту", attender.getName());
            attender.kill();
        } else
            logAndWait(log, "{} проходит каньон!", attender.getName());
        return attender;
    }
}
