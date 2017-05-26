package person.louchen.jsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Created by louchen on 2017/2/19.
 */
@SpringBootApplication
public class JspServer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JspServer.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(JspServer.class, args);
    }

}