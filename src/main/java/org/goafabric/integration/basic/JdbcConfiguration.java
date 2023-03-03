package org.goafabric.integration.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.jdbc.JdbcPollingChannelAdapter;
import org.springframework.messaging.MessageChannel;

import javax.sql.DataSource;

@Configuration
@Profile("jdbc")
public class JdbcConfiguration {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    public IntegrationFlow pollingFlow(DataSource dataSource) {
        var messageSource = new JdbcPollingChannelAdapter(dataSource, "SELECT * FROM masterdata.person");
        return IntegrationFlow.from(messageSource,
                        c -> c.poller(Pollers.fixedRate(1000).maxMessagesPerPoll(1)))
                .channel(jdbcChannel())
                .get();
    }

    @Bean
    public IntegrationFlow outputFlow() {
        return IntegrationFlow.from(jdbcChannel())
                .handle(message -> {
                            log.info("## got message " + message.getPayload());
                        }
                )
                .get();
    }

    @Bean
    public MessageChannel jdbcChannel() {
        return new DirectChannel();
    }


}
