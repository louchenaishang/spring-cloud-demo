package person.louchen.springcloud.business.service.feign.remote;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import person.louchen.springcloud.business.service.config.CommonFeignConfiguration;
import person.louchen.springcloud.business.service.feign.remote.fallback.HiServiceFallback;

/**
 * Created by louchen on 2017/4/23.
 */
@FeignClient(name = "business-service", fallback = HiServiceFallback.class, configuration = CommonFeignConfiguration.class)
public interface HiService {

    @RequestMapping(value = "/hi", method = RequestMethod.GET)
    String sayHi();

}
