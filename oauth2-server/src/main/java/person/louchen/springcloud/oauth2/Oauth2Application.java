package person.louchen.springcloud.oauth2;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * Created by louchen on 2017/5/17.
 */
@SpringBootApplication
@EnableAuthorizationServer
public class Oauth2Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Oauth2Application.class).web(true).run(args);
    }

}
