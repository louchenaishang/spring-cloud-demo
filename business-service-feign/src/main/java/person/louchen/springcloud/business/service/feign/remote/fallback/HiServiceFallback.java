package person.louchen.springcloud.business.service.feign.remote.fallback;

import org.springframework.stereotype.Component;
import person.louchen.springcloud.business.service.feign.remote.HiService;

/**
 * Created by louchen on 2017/5/15.
 */
@Component
public class HiServiceFallback implements HiService {

    @Override
    public String sayHi() {
        return "hystrix fallback:sayHi Method";
    }
}
