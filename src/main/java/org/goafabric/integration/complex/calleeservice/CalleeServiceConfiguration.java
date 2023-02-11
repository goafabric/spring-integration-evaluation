package org.goafabric.integration.complex.calleeservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;

@Slf4j
@Configuration
@Profile("service")
public class CalleeServiceConfiguration {
    //curl http://localhost:50900/callees/sayMyName?name=slim
    @Bean
    public IntegrationFlow inputFlow() {
        var messageSource = Http.inboundChannelAdapter("/callees/sayMyName")
                .requestMapping(r -> r.methods(HttpMethod.GET).params("name"));

        return IntegrationFlow.from(messageSource)
                .channel("httpChannel") //if we use just a literal, channel will be created automatically
                .get();
    }
    @Bean
    public IntegrationFlow outputFlow() {
        return IntegrationFlow.from("httpChannel")
                .handle(message -> log.info("## got message " + message.getPayload()))
                .get();
    }
}
