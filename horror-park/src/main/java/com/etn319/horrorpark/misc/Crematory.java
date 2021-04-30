package com.etn319.horrorpark.misc;

import com.etn319.horrorpark.domain.Attender;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.Collection;

@MessagingGateway
public interface Crematory {
    @Gateway(replyChannel = "crematoryChannel", replyTimeout = 1000)
    Collection<Attender> visit();
}
