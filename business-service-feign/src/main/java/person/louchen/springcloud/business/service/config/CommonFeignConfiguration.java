package person.louchen.springcloud.business.service.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by louchen on 2017/5/15.
 */
@Configuration
public class CommonFeignConfiguration {

    @Value("${microservice.username}")
    private String microserviceUsername;

    @Value("${microservice.password}")
    private String microservicePassword;

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(microserviceUsername, microservicePassword);
    }

}
