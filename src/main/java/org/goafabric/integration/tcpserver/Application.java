/*
package org.goafabric.integration.tcpserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
    @Bean
    public CommandLineRunner init(ApplicationContext context) {
        //context.getBean(MyNewerConfiguration.Gateway.class).send("yo");
        //context.getBean(MyConfiguration.class).createMessage();
        //IntegrationConfiguration.integration(kafkaChannel);
        int x = 5;
        return args -> {if ((args.length > 0) && ("-check-integrity".equals(args[0]))) {SpringApplication.exit(context, () -> 0);}};
    }


}

*/