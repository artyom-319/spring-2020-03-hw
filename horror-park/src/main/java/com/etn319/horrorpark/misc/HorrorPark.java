package com.etn319.horrorpark.misc;

import com.etn319.horrorpark.domain.AttenderGroup;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface HorrorPark {
    @Gateway(requestChannel = "entranceChannel", replyChannel = "exitChannel")
    AttenderGroup enter(AttenderGroup attenders);
}
