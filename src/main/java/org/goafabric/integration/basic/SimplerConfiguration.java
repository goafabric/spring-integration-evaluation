package org.goafabric.integration.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.support.GenericMessage;

@Configuration
@Profile("simpler")
public class SimplerConfiguration {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private boolean triggerEnabled = true;

    @Bean
    public IntegrationFlow inputFlow() {
        return IntegrationFlow.from(() -> {
                    log.info("# checking simpleR inbound trigger");
                    return triggerEnabled ? new GenericMessage<>("hit me baby") : null; //emit message if triggerEnabled to be caught by outbound
                })
                .channel("simplerChannel") //if we use just a literal, channel will be created automatically
                .get();
    }
    @Bean
    public IntegrationFlow outputFlow() {
        return IntegrationFlow.from("simplerChannel")
                .handle(message -> log.info("## got message " + message.getPayload()))
                .get();
    }

}
