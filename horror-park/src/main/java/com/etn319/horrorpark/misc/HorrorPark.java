package com.etn319.horrorpark.misc;

import com.etn319.horrorpark.domain.AttenderGroup;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface HorrorPark {
    @Gateway(requestChannel = "entranceChannel", replyChannel = "exitChannel", replyTimeout = 2 * 60 * 1000)
    AttenderGroup enter(AttenderGroup attenders);
}
