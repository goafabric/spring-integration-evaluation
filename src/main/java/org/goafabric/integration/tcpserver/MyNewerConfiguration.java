/*
package org.goafabric.integration.tcpserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
//@IntegrationComponentScan
public class MyNewerConfiguration {
    private static final String INBOX = "inbox";

    @MessagingGateway(defaultRequestChannel = "inbox")
    public interface Gateway {
        String send(String msg);
    }

    @Bean
    public IntegrationFlow flow() {
        return IntegrationFlow.from("application.inbox")
                .transform(p -> "world")
                .get();
    }

    @Bean(name = INBOX)
    public MessageChannel inbox() {
        return new DirectChannel();
    }
}

 */
