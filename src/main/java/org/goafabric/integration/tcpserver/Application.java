package org.goafabric.integration.tcpserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.file.DefaultDirectoryScanner;
import org.springframework.integration.file.DirectoryScanner;


@SpringBootApplication
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
    @Bean
    public CommandLineRunner init(ApplicationContext context) {
        DirectoryScanner scanner = new DefaultDirectoryScanner();
        return args -> {if ((args.length > 0) && ("-check-integrity".equals(args[0]))) {SpringApplication.exit(context, () -> 0);}};
    }
}