package com.etn319.horrorpark.config;

import com.etn319.horrorpark.domain.Attender;
import com.etn319.horrorpark.domain.AttenderGroup;
import com.etn319.horrorpark.domain.DeadAttender;
import com.etn319.horrorpark.integration.AliveCountReleaseStrategy;
import com.etn319.horrorpark.integration.AttenderGroupAggregator;
import com.etn319.horrorpark.misc.AttenderGroupCoordinator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.RecipientListRouterSpec;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.PollableChannel;

import java.util.function.Consumer;

@Configuration
public class IntegrationConfig {
    @Autowired
    private AttenderGroupCoordinator groupCoordinator;

    @Bean
    public QueueChannel entranceChannel() {
        return MessageChannels.queue(5).get();
    }

    @Bean
    public QueueChannel meetingPointChannel() {
        return MessageChannels.queue().get();
    }

    @Bean
    public QueueChannel exitChannel() {
        return MessageChannels.queue(3).get();
    }

    @Bean
    public PollableChannel crematoryChannel() {
        return MessageChannels.queue(5).get();
    }

    @Bean
    public PollableChannel transformerChannel() {
        return MessageChannels.queue().get();
    }

    @Bean
    public AttenderGroupAggregator attenderGroupAggregator() {
        return new AttenderGroupAggregator();
    }

    @Bean
    public AliveCountReleaseStrategy aliveCountReleaseStrategy() {
        return new AliveCountReleaseStrategy(groupCoordinator);
    }

    public Consumer<RecipientListRouterSpec> deadAttenderRedirector(String recipientChannel) {
        return spec -> spec
                .recipient(recipientChannel, Attender::isDead)
                .defaultOutputToParentFlow();
    }

    @Transformer(inputChannel = "transformerChannel", outputChannel = "meetingPointChannel")
    public Attender transformer(Attender attender) {
        return new DeadAttender(attender);
    }

    @Bean
    public IntegrationFlow horrorParkFlow() {
        return IntegrationFlows.from("entranceChannel")
                .handle("monsterRoom", "accept")
                .split(AttenderGroup.class, AttenderGroup::getAttenders)
                .routeToRecipients(deadAttenderRedirector("transformerChannel"))
                .<Attender, Boolean>route(at -> at.getHealth().getValue() > 5,
                        mapping -> mapping
                        .subFlowMapping(true, sf -> sf
                                .handle("mineField", "accept")
                        )
                        .subFlowMapping(false, sf -> sf
                                .handle("canyon", "accept")
                        )
                )
                .routeToRecipients(deadAttenderRedirector("transformerChannel"))
                .channel("meetingPointChannel")
                .aggregate(spec -> spec.processor(attenderGroupAggregator(), "aggregator"))
                .split(AttenderGroup.class, AttenderGroup::getAttenders)
                .routeToRecipients(deadAttenderRedirector("crematoryChannel"))
                .aggregate(spec -> spec.processor(attenderGroupAggregator(), "aggregator")
                        .releaseStrategy(aliveCountReleaseStrategy()))
                .channel("exitChannel")
                .get();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedRate(100).maxMessagesPerPoll(2).get();
    }
}
