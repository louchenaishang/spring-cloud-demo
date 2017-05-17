package person.louchen.springcloud.business.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Created by louchen on 2017/2/19.
 */
@SpringBootApplication
@EnableEurekaClient
public class BusinessServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BusinessServiceApplication.class).web(true).run(args);
    }

}