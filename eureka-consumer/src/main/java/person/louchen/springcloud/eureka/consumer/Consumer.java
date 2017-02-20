package person.louchen.springcloud.eureka.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by louchen on 2017/2/19.
 */
@SpringBootApplication
@RestController
@EnableDiscoveryClient
@RibbonClient(name = "eureka-client", configuration = SampleRibbonConfiguration.class)
public class Consumer {

    @LoadBalanced
    @Bean
    protected RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/")
    public String home() {
        return this.restTemplate.getForObject("http://eureka-client/", String.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Consumer.class, args);
    }

}
