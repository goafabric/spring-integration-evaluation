package org.goafabric.integration.tcpserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;

@Slf4j
@Configuration
@Profile("simple")
public class SimpleConfiguration {
    private boolean triggerEnabled = true;

    @Bean
    public IntegrationFlow inputFlow() {
        return IntegrationFlow.from(new MessageSource<Object>() {
                    @Override
                    public Message<Object> receive() {
                        log.info("# checking inbound trigger");
                        return triggerEnabled ? new GenericMessage<>("hit me baby") : null;
                    }
                }, config -> config.poller(Pollers.fixedDelay(1000)))
                .channel(logChannel())
                .get();
    }
    @Bean
    public IntegrationFlow outputFlow() {
        return IntegrationFlow.from(logChannel())
                .handle(new MessageHandler() {
                    @Override
                    public void handleMessage(Message<?> message) throws MessagingException {
                        log.info("## got message " + message.getPayload());
                    }
                })
                .get();
    }

    @Bean
    public MessageChannel logChannel() { return new QueueChannel(); }  // don't dare to use DirectChannel it will fail

    @Bean
    public void testMe() {
        //logChannel().send(new GenericMessage<>("one more time"));
    }

}
