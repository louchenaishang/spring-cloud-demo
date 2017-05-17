package person.louchen.springcloud.business.service.feign.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import person.louchen.springcloud.business.service.feign.remote.HiService;

/**
 * Created by louchen on 2017/2/19.
 */
@RestController
public class HelloController {

    @Autowired
    private HiService hiService;

    @RequestMapping("/hi")
    public String home() {
        return hiService.sayHi();
    }

}
