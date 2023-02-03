package org.goafabric.integration.tcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;


/**
 * Created by amautsch on 26.06.2015.
 */

@SpringBootApplication
//todo: yuck this should be java configuration
@ImportResource("classpath:META-INF/spring/integration/tcpClientServerDemo-context.xml")
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }

}
