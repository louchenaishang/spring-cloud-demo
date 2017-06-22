package person.louchen.rest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by louchen on 2017/6/22.
 */
@SpringBootApplication
public class RestJwtServer {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RestJwtServer.class).web(true).run(args);
    }

}
