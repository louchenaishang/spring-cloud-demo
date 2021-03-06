package peron.louchen.rest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by louchen on 2017/5/19.
 */
@RestController
public class Rest {

    @GetMapping("/hi")
    public String hi(){
        return "hi";
    }

    @GetMapping("/user/hi")
    public String user(){
        return "user/hi";
    }


    @GetMapping("/admin/hi")
    public String admin(){
        return "admin/hi";
    }

    @GetMapping("/anonymous/hi")
    public String anonymous(){
        return "anonymous/hi";
    }

}
