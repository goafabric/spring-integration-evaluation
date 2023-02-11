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
//needs spring-boot-starter-web
public class CalleeServiceConfiguration {

    @Bean
    public IntegrationFlow sayMyName() {
        return IntegrationFlow.from(Http.inboundChannelAdapter("/callees/sayMyName")
                        .requestMapping(r -> r.methods(HttpMethod.GET).params("name")))
                .handle(message -> log.info("your name is : " + ((LinkedMultiValueMap) message.getPayload()).get("name")))
                .get();
    }

    @Bean
    public IntegrationFlow sayMyOtherName() {
        return IntegrationFlow.from(Http.inboundChannelAdapter("/callees/sayMyOtherName")
                        .requestMapping(r -> r.methods(HttpMethod.GET).params("name")))
                .handle(message -> log.info("your other name is : " + ((LinkedMultiValueMap) message.getPayload()).get("name")))
                .get();
    }
}
