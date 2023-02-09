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
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

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
                //.transform(processor())
                .get();
    }

    @Bean
    public IntegrationFlow personItemWriterFlow(PersonItemProcessor processor) {
        return IntegrationFlow.from(jdbcChannel())
                .handle(message -> {
                            List<Person> persons = (List<Person>) message.getPayload();
                            log.info("## got message " + persons.toString());
                        }
                )
                .get();
    }

    @Bean
    public MessageChannel jdbcChannel() {
        return new DirectChannel();
    }

    @Bean
    public Transformer processor() {
        return new AbstractPayloadTransformer<List<Person>, List<Person>>() {
            @Override
            protected List<Person> transformPayload(List<Person> payload) {
                return payload;
            }
        };
    }

    @Component
    static class PersonItemProcessor {
        public Person process(Person person) {
            return person;
        }
    }
    /*
    class PersonItemProcessor extends AbstractPayloadTransformer<List<Person>, List<Person>> {

        @Override
        protected List<Person> transformPayload(List<Person> payload) {
            return payload;
        }

        private Person process(Person person) {
            return person;
        }
    }

     */


}
