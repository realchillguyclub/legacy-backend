package server.poptato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@EnableScheduling
public class PoptatoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoptatoApplication.class, args);
        System.out.println("Default Time Zone: " + java.util.TimeZone.getDefault().getID());
        System.out.println("Hello Poptato Server Team !!");
    }

}
