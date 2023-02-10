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
import org.springframework.integration.transformer.Transformer;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.messaging.MessageChannel;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@Profile("anonymize")
public class PersonAnonymizerConfiguration {

    @Bean
    public IntegrationFlow personItemReader(DataSource dataSource, PersonItemProcessor processor) {
        var messageSource = new JdbcPollingChannelAdapter(dataSource, "SELECT * FROM masterdata.person");
        messageSource.setRowMapper(new BeanPropertyRowMapper<>(Person.class));
        return IntegrationFlow.from(messageSource,
                        c -> c.poller(Pollers.fixedRate(1000).maxMessagesPerPoll(1)))
                .transform(transformer())
                .channel(jdbcChannel())
                .get();
    }

    @Bean
    public IntegrationFlow personItemWriter(PersonItemProcessor processor) {
        return IntegrationFlow.from(jdbcChannel())
                .handle(message -> {
                            List<Person> persons = (List<Person>) message.getPayload();
                            log.info("## got message " + persons);
                        }
                )
                .get();
    }


    @Bean
    public Transformer transformer() {
        return new AbstractPayloadTransformer<List<Person>, List<Person>>() {
            @Override
            protected List<Person> transformPayload(List<Person> payload) {
                return payload.stream().map(
                        person -> personItemProcessor().process(person)).collect(Collectors.toList());
            }
        };
    }

    @Bean
    public PersonItemProcessor personItemProcessor () {
        return new PersonItemProcessor();
    }

    @Bean
    public MessageChannel jdbcChannel() {
        return new DirectChannel();
    }

}
