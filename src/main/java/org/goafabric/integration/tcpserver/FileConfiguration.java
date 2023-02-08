package org.goafabric.integration.tcpserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Configuration
@Profile("file")
public class FileConfiguration {
    public String INPUT_DIR = "/Users/andreas/Downloads/inbound";
    public String OUTPUT_DIR = "/Users/andreas/Downloads/outbound";
    public String FILE_PATTERN = "*.*";

    @Bean
    public MessageChannel fileChannel() {
        return new DirectChannel();
    }

    /*
    @Bean
    @InboundChannelAdapter(value = "fileChannel", poller = @Poller(fixedDelay = "1000"))
    public MessageSource<File> fileReadingMessageSource() {
        FileReadingMessageSource sourceReader= new FileReadingMessageSource();
        sourceReader.setDirectory(new File(INPUT_DIR));
        sourceReader.setFilter(new SimplePatternFileListFilter(FILE_PATTERN));
        return sourceReader;
    }

     */

    @Bean
    @ServiceActivator(inputChannel= "fileChannel")
    public MessageHandler fileWritingMessageHandler() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(OUTPUT_DIR));
        handler.setFileExistsMode(FileExistsMode.REPLACE);
        handler.setExpectReply(false);
        return handler;
    }

    @Bean
    public IntegrationFlow fileReadingFlow() {
        return IntegrationFlow
                .from(Files.inboundAdapter(new File(INPUT_DIR)).patternFilter(FILE_PATTERN),
                        e -> e.poller(Pollers.fixedDelay(1000)))
                //.transform(Files.toStringTransformer())
                .channel("fileChannel")
                .get();
    }
}
