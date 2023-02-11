package org.goafabric.integration.complex.calleeservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.util.LinkedMultiValueMap;

@Slf4j
@Configuration
@Profile("service")
public class CalleeServiceConfiguration {
    //curl -m 1 "http://localhost:50900/callees/sayMyName?name=yo"
    @Bean
    public IntegrationFlow sayMyName() {
        var messageSource = Http.inboundChannelAdapter("/callees/sayMyName")
                .requestMapping(r -> r.methods(HttpMethod.GET).params("name"));

        return IntegrationFlow.from(messageSource)
                .handle(message -> log.info("your name is : " + ((LinkedMultiValueMap)message.getPayload()).get("name")))
                .get();
    }
}
