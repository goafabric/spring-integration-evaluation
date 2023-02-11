package org.goafabric.integration.basic;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;

@Slf4j
@Configuration
@Profile("kafka")
public class KafkaConfiguration {

    @Bean
    public IntegrationFlow flow1(ConsumerFactory<String, String> cf)  {
        var props = new ConsumerProperties("main.topic");
        props.setGroupId("Calendar");
        return IntegrationFlow.from(Kafka.inboundChannelAdapter(cf, props), e -> e.poller(Pollers.fixedDelay(1000)))
                .handle(message -> {
                    processKafka(new String((byte[]) message.getHeaders().get(KafkaHeaders.RECEIVED_KEY)),
                           (EventData) message.getPayload());
                })
                .get();
    }

    public void processKafka(@Header(KafkaHeaders.RECEIVED_KEY) @NonNull String key, @NonNull EventData eventData) {
        log.info(key + " " + eventData.getReferenceId());
    }


    @Bean
    IntegrationFlow outFlow(MessageChannel kafkaChannel, KafkaTemplate<Object, Object> template) {
        var kafka = Kafka
                .outboundChannelAdapter(template)
                .topic("main.topic")
                //.headerMapper(new DefaultKafkaHeaderMapper())
                .headerMapper(new KafkaHeaderMapper() {
                    @Override
                    public void fromHeaders(MessageHeaders headers, Headers target) {
                        target.add(KafkaHeaders.RECEIVED_KEY, headers.get(KafkaHeaders.RECEIVED_KEY, String.class).getBytes());
                    }

                    @Override
                    public void toHeaders(Headers source, Map<String, Object> target) {

                    }
                })
                .get();
        return IntegrationFlow
                .from(kafkaChannel)
                .handle(kafka)
                .get();
    }
    @Bean
    public MessageChannel kafkaChannel() {
        return new QueueChannel();
    }

    @Bean
    public void sendMe() {
        var message = MessageBuilder
                .withPayload(new EventData("1000"))
                .copyHeaders(Map.of(KafkaHeaders.TOPIC, "main.topic"))
                .copyHeaders(Map.of(KafkaHeaders.RECEIVED_KEY, "patient.create"))
                .build();
        kafkaChannel().send(message);
    }

    @Value
    private static class EventData {
        private String referenceId;
    }

}
