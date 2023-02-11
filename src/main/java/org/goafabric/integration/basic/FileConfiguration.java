package org.goafabric.integration.basic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;

import java.io.File;

@Configuration
@Profile("file")
public class FileConfiguration {
    public String INPUT_DIR = "/Users/andreas/Downloads/inbound";
    public String OUTPUT_DIR = "/Users/andreas/Downloads/outbound";
    public String FILE_PATTERN = "*.*";

    @Bean
    public IntegrationFlow fileReadingFlow() {
        var messageSource = Files.inboundAdapter(new File(INPUT_DIR)).patternFilter(FILE_PATTERN).get();
        return IntegrationFlow
                .from(messageSource, config -> config.poller(Pollers.fixedDelay(1000))) //inbound adapter goes to from, if we omit the poller it will create one with 100ms
                .transform(Files.toStringTransformer())
                .channel(fileChannel()) //we could use "fileChannel" as literal, then the fileChannel Bean could be omited as it will be autocreated
                .get();
    }

    @Bean
    public IntegrationFlow fileWritingFlow() {
        var messageHandler = Files.outboundAdapter(new File(OUTPUT_DIR)).fileExistsMode(FileExistsMode.REPLACE).get();
        return IntegrationFlow.from(fileChannel())
                .handle(messageHandler) //outbound adapter goes to handle, with the channel going to from
                .get();
    }

    @Bean
    public MessageChannel fileChannel() {
        return new DirectChannel(); //we could omit the channel declaration
    }

}
