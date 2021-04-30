package com.etn319.horrorpark.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "horror-park")
@Data
public class ApplicationProperties {
    private MonsterProperties monster = new MonsterProperties();
    private MineFieldProperties mineField = new MineFieldProperties();
    private CanyonProperties canyon = new CanyonProperties();

    @Data
    static class MonsterProperties {
        private int agility;
        private int intellect;
        private int minDamage;
        private int maxDamage;
    }

    @Data
    static class MineFieldProperties {
        private int length;
        private double mineChance;
        private int minDamage;
        private int maxDamage;
    }

    @Data
    static class CanyonProperties {
        private int bridgeWidth;
        private int windSpeed;
        private int luckyFactor;
    }
}
