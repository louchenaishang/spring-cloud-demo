package person.louchen.springcloud.business.service.rest;

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

    @Value("${cloud.author}")
    protected String author;


    @RequestMapping("/hi")
    public String home() {
        return "port:" + port + ",author:" + author + ",Hello world";
    }

}