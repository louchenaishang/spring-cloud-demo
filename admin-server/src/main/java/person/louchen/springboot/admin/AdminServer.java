package person.louchen.springboot.admin;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;

/**
 * Created by louchen on 2017/5/18.
 */
@Configuration
@EnableAutoConfiguration
@EnableEurekaClient
@EnableAdminServer
public class AdminServer {

    public static void main(String[] args) {
        SpringApplication.run(AdminServer.class, args);
    }

}
