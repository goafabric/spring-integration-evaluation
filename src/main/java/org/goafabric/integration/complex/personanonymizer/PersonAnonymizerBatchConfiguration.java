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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.messaging.MessageChannel;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@Profile("batch")
public class PersonAnonymizerBatchConfiguration {

    @Bean
    public IntegrationFlow personItemReader(DataSource dataSource) {
        var messageSource = new JdbcPollingChannelAdapter(dataSource, "SELECT * FROM masterdata.person");
        messageSource.setRowMapper(new BeanPropertyRowMapper<>(Person.class));
        return IntegrationFlow.from(messageSource,
                        c -> c.poller(Pollers.fixedRate(1000).maxMessagesPerPoll(1)))
                .transform(personItemTransformer())
                .channel(jdbcChannel())
                .get();
    }

    @Bean
    public IntegrationFlow personItemWriter(NamedParameterJdbcTemplate template) {
        final String sql = "UPDATE masterdata.person SET first_name = :firstName, last_name = :lastName WHERE id = :id";
        return IntegrationFlow.from(jdbcChannel())
                .handle(message -> {
                            List<Person> persons = (List<Person>) message.getPayload();
                            template.batchUpdate(sql, SqlParameterSourceUtils.createBatch(persons));
                            log.info("## got message " + persons);
                        }
                )
                .get();
    }

    @Bean
    public Transformer personItemTransformer() {
        return new AbstractPayloadTransformer<List<Person>, List<Person>>() {
            @Override
            protected List<Person> transformPayload(List<Person> payload) {
                return payload.stream().map(
                        person -> process(person)).collect(Collectors.toList());
            }

            private Person process(Person person) {
                person.setFirstName("fake firstName");
                person.setLastName("fake lastName");
                return person;
            }
        };
    }

    @Bean
    public MessageChannel jdbcChannel() {
        return new DirectChannel();
    }

}
