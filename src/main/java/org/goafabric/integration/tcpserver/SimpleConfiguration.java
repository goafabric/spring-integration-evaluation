package org.goafabric.integration.tcpserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

@Slf4j
@Configuration
//@EnableIntegration
@Profile("simple")
public class SimpleConfiguration {

    //this is what Integration actually calls an OutboundAdapter or @ServiceActivator(inputChannel =  "logChannel") in not DSL
    @Bean
    public IntegrationFlow logMessage() {
        return IntegrationFlow.from(logChannel())
                .handle(message -> log.info("## got message " + message.getPayload()))
                .get();
    }

    /*
    @ServiceActivator(inputChannel =  "logChannel")
    void logMessage(Object payload) { log.info(payload.toString()); }
    */

    @Bean
    public MessageChannel logChannel() { return new QueueChannel(); }  // don't dare to use DirectChannel it will fail

    @Bean
    public MessageChannel myOtherChannel() { return new QueueChannel(); }

    @Bean
    public void testMe() {
        logChannel().send(new GenericMessage<>("hit me baby"));
        logChannel().send(new GenericMessage<>("one more time"));
        myOtherChannel().send(new GenericMessage<>("do not route me"));
    }

}
