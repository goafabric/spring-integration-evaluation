package org.goafabric.integration.complex.personanonymizer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.jdbc.JdbcPollingChannelAdapter;
import org.springframework.integration.transformer.AbstractPayloadTransformer;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.messaging.MessageChannel;

import javax.sql.DataSource;

@Slf4j
@Configuration
@Profile("anonymize")
public class PersonAnonymizerConfiguration {

    @Bean
    public IntegrationFlow personItemReaderFlow(DataSource dataSource) {
        var messageSource = new JdbcPollingChannelAdapter(dataSource, "SELECT * FROM masterdata.person");
        messageSource.setRowMapper(new BeanPropertyRowMapper<>(Person.class));
        return IntegrationFlow.from(messageSource,
                        c -> c.poller(Pollers.fixedRate(1000).maxMessagesPerPoll(1)))
                .channel(jdbcChannel())
                .transform(new AbstractPayloadTransformer<>() {
                    @Override
                    protected Object transformPayload(Object payload) {
                        return payload;
                    }
                })
                .get();
    }

    @Bean
    public IntegrationFlow personItemWriterFlow() {
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
