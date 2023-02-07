/*
package org.goafabric.integration.tcpserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;


@Configuration
@EnableIntegration
public class MyNewConfiguration {
    @InboundChannelAdapter(channel = "myChannel", poller = @Poller(fixedRate = "5000"))
    Object method1() {
        System.err.println("got message !");
        return null;
    }

    @MessagingGateway(defaultRequestChannel = "myChannel")
    public interface Gateway {
        String send(String msg);
    }

    @Bean
    public MessageChannel myChannel() {
        //return new QueueChannel();
        return new DirectChannel();
        //return new PublishSubscribeChannel();
    }

    public void createMessage(Gateway gw) {
        gw.send("yo");
        //var message = MessageBuilder.createMessage(new Object(), new MessageHeaders(Map.of("yo", "")));
        //myChannel().send(message);
    }
    
}


 */