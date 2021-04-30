package com.etn319.horrorpark.config;

import com.etn319.horrorpark.misc.AttenderGroupCoordinator;
import com.etn319.horrorpark.stages.Canyon;
import com.etn319.horrorpark.stages.MineField;
import com.etn319.horrorpark.stages.stageprops.Monster;
import com.etn319.horrorpark.stages.MonsterRoom;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfig {
    @Bean
    public MineField mineField(ApplicationProperties appProps) {
        var props = appProps.getMineField();
        return new MineField(props.getLength(), props.getMineChance(), props.getMinDamage(), props.getMaxDamage());
    }

    @Bean
    public MonsterRoom monsterRoom(Monster monster) {
        return new MonsterRoom(monster);
    }

    @Bean
    public Canyon canyon(ApplicationProperties appProps) {
        var props = appProps.getCanyon();
        return new Canyon(props.getBridgeWidth(), props.getWindSpeed(),props.getLuckyFactor());
    }

    @Bean
    public Monster monster(ApplicationProperties appProps) {
        var props = appProps.getMonster();
        return new Monster(props.getAgility(), props.getIntellect(), props.getMinDamage(), props.getMaxDamage());
    }

    @Bean
    public AttenderGroupCoordinator attenderGroupCoordinator() {
        return new AttenderGroupCoordinator();
    }
}
