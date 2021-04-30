package com.etn319.horrorpark.config;

import com.etn319.horrorpark.domain.Attender;
import com.etn319.horrorpark.domain.AttenderGroup;
import com.etn319.horrorpark.misc.AttenderGroupCoordinator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.PublishSubscribeChannel;
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
    public QueueChannel afterForkChannel() {
        return MessageChannels.queue().get();
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
    public PublishSubscribeChannel crematoryChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public PollableChannel pollableCrematoryChannel() {
        return MessageChannels.queue(5).get();
    }

    @Bean
    public PollableChannel transformerChannel() {
        return MessageChannels.queue().get();
    }

    @Bean
    public AttenderGroupAggregator customAggregator() {
        return new AttenderGroupAggregator(groupCoordinator);
    }

    @Bean
    public Consumer<RecipientListRouterSpec> deadAttenderRedirector() {
        return spec -> spec
                .recipient("transformerChannel", Attender::isDead)
                .defaultOutputToParentFlow();
    }

    @Transformer(inputChannel = "transformerChannel", outputChannel = "meetingPointChannel")
    public Attender transformer(Attender attender) {
        return attender;
    }

    @Bean
    public IntegrationFlow horrorParkFlow() {
        return IntegrationFlows.from("entranceChannel")
                .handle("monsterRoom", "accept")
                .split(AttenderGroup.class, AttenderGroup::getAttenders)
                .routeToRecipients(deadAttenderRedirector())
                .<Attender, Boolean>route(at -> at.getHealth().getValue() > 5,
                        mapping -> mapping
                        .subFlowMapping(true, sf -> sf
                                .handle("mineField", "accept")
                        )
                        .subFlowMapping(false, sf -> sf
                                .handle("canyon", "accept")
                        )
                )
                .routeToRecipients(deadAttenderRedirector())
                .channel("meetingPointChannel")
                .log()
                // todo: divide aggregator and release strategy
                .aggregate(spec -> spec.processor(customAggregator(), "aggregator"))
                .log()
                .channel("exitChannel")
                .get();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedRate(100).maxMessagesPerPoll(2).get();
    }
}
