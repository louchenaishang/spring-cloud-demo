package person.louchen.springcloud.business.service.feign;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * Created by louchen on 2017/2/19.
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
public class BusinessServiceFeignApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BusinessServiceFeignApplication.class).web(true).run(args);
    }

}
