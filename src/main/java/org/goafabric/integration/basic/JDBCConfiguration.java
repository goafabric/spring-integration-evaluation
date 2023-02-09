package org.goafabric.integration.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.jdbc.JdbcPollingChannelAdapter;

import javax.sql.DataSource;

@Slf4j
@Configuration
@Profile("jdbc")
public class JDBCConfiguration {

    @Bean
    public IntegrationFlow pollingFlow(DataSource dataSource) {
        var messageSource = new JdbcPollingChannelAdapter(dataSource, "SELECT * FROM something");;
        return IntegrationFlow.from(messageSource,
                        c -> c.poller(Pollers.fixedRate(100).maxMessagesPerPoll(1)))
                .transform(Transformers.toJson())
                .channel("jdbcChannel")
                .get();
    }

    @Bean
    public IntegrationFlow outputFlow() {
        return IntegrationFlow.from("jdbcChannel")
                .handle(message -> log.info("## got message " + message.getPayload()))
                .get();
    }


    @Bean
    public DataSource dataSource()
    {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:person");
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }

}
