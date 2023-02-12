package org.goafabric.integration.basic;

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
        return IntegrationFlow.from(new MessageSource<Object>() { //for input we need a MessageSource inside from(), InputAdapters are just that
                    @Override
                    public Message<Object> receive() {
                        log.info("# checking simple inbound trigger");
                        return triggerEnabled ? new GenericMessage<>("hit me baby") : null; //emit message if triggerEnabled to be caught by outbound
                    }
                }, config -> config.poller(Pollers.fixedDelay(1000))) //there will always be a poller, if we omit the config it will create one with 100ms
                .channel(simpleChannel()) //channel goes here
                .get();
    }
    @Bean
    public IntegrationFlow outputFlow() {
        return IntegrationFlow.from(simpleChannel()) // channel goes here in from
                .handle(new MessageHandler() { //for output we need a MessageHandler inside handle(), OutputAdapters are just that
                    @Override
                    public void handleMessage(Message<?> message) throws MessagingException {
                        log.info("## got message " + message.getPayload());
                    }
                })
                .get();
    }

    @Bean
    public MessageChannel simpleChannel() { return new QueueChannel(); }  //needs to be QueueChannel if we want to be able to send Messages

    /*
    @Bean
    public void testMe() {
        simpleChannel().send(new GenericMessage<>("one more time"));
    }
    */

}
