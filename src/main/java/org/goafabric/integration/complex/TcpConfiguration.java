package org.goafabric.integration.complex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@Profile("tcp")
public class TcpConfiguration {
    final int port = 5001;
    @Bean
    public IntegrationFlow tcpFlow() {
        var messageSource = Tcp.inboundAdapter(Tcp.netServer(port).deserializer(new ByteArrayCrLfSerializer())).get();
        return IntegrationFlow
                .from(messageSource)
                .transform(Transformers.objectToString())
                .channel(tcpChannel())
                .get();
    }

    @Bean
    public IntegrationFlow outputFlow(SimpMessageSendingOperations template) {
        return IntegrationFlow.from(tcpChannel())
                .handle(message -> {
                    log.info("## got message " + message.getPayload());
                    template.convertAndSend("/public", message.getPayload());
                })
                .get();
    }

    @Bean
    public WebSocketMessageBrokerConfigurer webSocketConfig() {
        return new WebSocketMessageBrokerConfigurer() {
            @Override
            public void registerStompEndpoints(StompEndpointRegistry registry) {
                registry.addEndpoint("/websocket").withSockJS();
            }
        };
    }

    @Bean
    public MessageChannel tcpChannel() {
        return new DirectChannel();
    }

}
