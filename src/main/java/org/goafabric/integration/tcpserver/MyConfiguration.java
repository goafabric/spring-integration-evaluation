package org.goafabric.integration.tcpserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

@Configuration
@EnableIntegration
@Slf4j
public class MyConfiguration {
    @Bean
    public IntegrationFlow inputFlow() {
        return IntegrationFlow.from(myChannel())
                .handle(message -> log.info("got message " + message.getPayload()))
                .get();
    }

    @Bean
    public MessageChannel myChannel() { return new QueueChannel(); }

    @Bean
    public MessageChannel myOtherChannel() { return new QueueChannel(); }

    @Bean
    public void testMe() {
        myChannel().send(new GenericMessage<>("hit me baby"));
        myChannel().send(new GenericMessage<>("one more time"));
        myOtherChannel().send(new GenericMessage<>("do not route me"));
    }

}
