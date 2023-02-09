package org.goafabric.integration.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
@Profile("tcp")
public class TcpConfiguration {
    final int port = 5001;
    @Bean
    public IntegrationFlow tcpFlow() {
        var messageSource = Tcp.inboundAdapter(Tcp.netServer(port).deserializer(new ByteArrayCrLfSerializer())).get();
        return IntegrationFlow
                .from(messageSource)
                .channel(tcpChannel())
                //.transform(new ObjectToStringTransformer())
                .get();
    }

    @Bean
    public IntegrationFlow outputFlow() {
        return IntegrationFlow.from(tcpChannel())
                .handle(message -> log.info("## got message " + new String((byte[]) message.getPayload())))
                .get();
    }

    @Bean
    public MessageChannel tcpChannel() {
        return new DirectChannel();
    }

}
