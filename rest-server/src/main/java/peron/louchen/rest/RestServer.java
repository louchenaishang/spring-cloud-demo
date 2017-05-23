package peron.louchen.rest;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by louchen on 2017/5/19.
 */
@SpringBootApplication
public class RestServer {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RestServer.class).web(true).run(args);
    }


    @EnableWebSecurity
    protected static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Getter
        @Setter
        protected static class CustomUserDetails implements UserDetails{

            private String name;
            private String pwd;

            public CustomUserDetails(){}

            public CustomUserDetails(String name,String pwd){
                this.name = name;
                this.pwd = pwd;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                return authorities;
            }

            @Override
            public String getPassword() {
                return pwd;
            }

            @Override
            public String getUsername() {
                return name;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

        }

        @Service
        protected static class CustomUserDetailsService implements UserDetailsService {

            private static final Map<String,CustomUserDetails> Users = new HashMap<>();

            static {
                Users.put("user",new CustomUserDetails("user","1"));
                Users.put("admin",new CustomUserDetails("admin","1"));
            }

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                CustomUserDetails user = Users.get(username);
                if(user==null){
                    throw new UsernameNotFoundException("Username not found");
                }
                return user;
            }

        }

        @Component
        protected static class CustomAuthenticationProvider  implements AuthenticationProvider {

            @Autowired
            private UserDetailsService userDetailsService;

            @Autowired
            private LoginAttemptService loginAttemptService;

            @Autowired
            private HttpServletRequest request;

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String ip = request.getRemoteAddr();
                if (loginAttemptService.isBlocked(ip)) {
                    throw new BadCredentialsException("ip blocked");
                }

                UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
                UserDetails userDetails = userDetailsService.loadUserByUsername(token.getName());
                if (userDetails == null) {
                    throw new UsernameNotFoundException("找不到该用户");
                }
                if(!userDetails.getPassword().equals(token.getCredentials().toString()))
                {
                    throw new BadCredentialsException("密码错误");
                }
                return new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(),userDetails.getAuthorities());
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.equals(authentication);
            }

        }

        @Component
        protected static class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler{

            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter writer = response.getWriter();
                writer.println("HTTP Status 401 : " + authenticationException.getMessage());
            }

        }

        @Component
        protected static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{

            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter writer = response.getWriter();
                writer.println("HTTP Status 401 : " + authenticationException.getMessage());
            }

        }

        @Service
        protected static class LoginAttemptService {

            private Logger logger = LoggerFactory.getLogger(this.getClass());
            private final int MAX_ATTEMPT = 5;
            private LoadingCache<String, Integer> attemptsCache;

            public LoginAttemptService() {
                super();
                attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
            }

            public void loginSucceeded(String key) {
                attemptsCache.invalidate(key);
            }

            public void loginFailed(String key) {
                int attempts = 0;
                try {
                    attempts = attemptsCache.get(key);
                } catch (ExecutionException e) {
                    attempts = 0;
                }
                attempts++;
                attemptsCache.put(key, attempts);
                if(attempts>=3){
                    logger.warn("ip:{},login failed attempt:{}",key,attempts);
                }else{
                    logger.info("ip:{},login failed attempt:{}",key,attempts);
                }
            }

            public boolean isBlocked(String key) {
                try {
                    return attemptsCache.get(key) >= MAX_ATTEMPT;
                } catch (ExecutionException e) {
                    return true;
                }
            }

        }

        @Component
        public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

            @Autowired
            private LoginAttemptService loginAttemptService;

            public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
                WebAuthenticationDetails auth = (WebAuthenticationDetails) e.getAuthentication().getDetails();
                loginAttemptService.loginFailed(auth.getRemoteAddress());
            }

        }

        @Component
        public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

            @Autowired
            private LoginAttemptService loginAttemptService;

            public void onApplicationEvent(AuthenticationSuccessEvent e) {
                WebAuthenticationDetails auth = (WebAuthenticationDetails) e.getAuthentication().getDetails();
                loginAttemptService.loginSucceeded(auth.getRemoteAddress());
            }

        }

//        @Bean
//        public BCryptPasswordEncoder passwordEncoder() {
//            return new BCryptPasswordEncoder();
//        }

//        @Bean
//        public UserDetailsService userDetailsService() {
//            InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//            manager.createUser(User.withUsername("user").password(passwordEncoder().encode("1")).roles("USER").build());
//            manager.createUser(User.withUsername("admin").password(passwordEncoder().encode("1")).roles("USER", "ADMIN").build());
//            return manager;
//        }

        @Autowired
        private AuthenticationProvider authenticationProvider;

        @Autowired
        private AuthenticationFailureHandler authenticationFailureHandler;

        @Autowired
        private AuthenticationEntryPoint authenticationEntryPoint;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider);
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests()
                        .antMatchers("/anonymous/**").permitAll()
                        .antMatchers("/public/**").permitAll()
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/**").hasRole("USER")
                        .anyRequest().authenticated()
                    .and()
                        .formLogin()
                            .usernameParameter("username")
                            .passwordParameter("password")
                            .failureHandler(authenticationFailureHandler)
                    .and()
                        .exceptionHandling()
                            .authenticationEntryPoint(authenticationEntryPoint)
                    .and()
                        .logout()
                            .invalidateHttpSession(true)
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                            .logoutSuccessHandler(new SimpleUrlLogoutSuccessHandler())
                            .addLogoutHandler(new SecurityContextLogoutHandler());
        }

    }
}
