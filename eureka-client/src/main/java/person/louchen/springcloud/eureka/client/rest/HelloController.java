package person.louchen.springcloud.eureka.client.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by louchen on 2017/2/19.
 */
@RestController
public class HelloController {

    @Value("${server.port}")
    protected String port;

    @RequestMapping("/")
    public String home() {
        return "port:" + port + ",Hello world";
    }

}
