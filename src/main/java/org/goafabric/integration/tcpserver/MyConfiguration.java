package org.goafabric.integration.tcpserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.messaging.*;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableIntegration
public class MyConfiguration {

    @Bean
    public AtomicInteger integerSource() {
        return new AtomicInteger();
    }

    @Bean
    public IntegrationFlow myFlow() {
        return IntegrationFlow.fromSupplier(integerSource()::getAndIncrement,
                        c -> c.poller(Pollers.fixedRate(100)))
                .channel("inputChannel")
                .filter((Integer p) -> p > 0)
                .transform(Object::toString)
                .channel(MessageChannels.queue())
                .get();
    }

    @Bean
    public IntegrationFlow inputFlow() {
        return IntegrationFlow.from("application.myChannel")
                .channel(MessageChannels.queue())
                .handle(new MessageHandler() {
                    @Override
                    public void handleMessage(Message<?> message) throws MessagingException {
                        System.err.println(message);
                    }
                })
                .get();
    }

    @Bean
    public MessageChannel myChannel() {
        return new DirectChannel();
    }

    public void createMessage() {
        var message = MessageBuilder.createMessage(new Object(), new MessageHeaders(Map.of("yo", "")));
        myChannel().send(message);
    }
}