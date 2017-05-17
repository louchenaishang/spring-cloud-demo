package service;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Created by louchen on 2017/2/19.
 */
@SpringBootApplication
@EnableEurekaClient
public class BusinessServiceOauth2Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BusinessServiceOauth2Application.class).web(true).run(args);
    }

}