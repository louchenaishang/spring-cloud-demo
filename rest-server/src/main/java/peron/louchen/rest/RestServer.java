package peron.louchen.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

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

        protected static class CustomAuthenticationProvider  implements AuthenticationProvider {

            private UserDetailsService userDetailsService;

            public CustomAuthenticationProvider(){}

            public CustomAuthenticationProvider(UserDetailsService userDetailsService){
                this.userDetailsService = userDetailsService;
            }

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
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

        protected static class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler{

            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter writer = response.getWriter();
                writer.println("HTTP Status 401 : " + authenticationException.getMessage());
            }

        }

        protected static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{

            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter writer = response.getWriter();
                writer.println("HTTP Status 401 : " + authenticationException.getMessage());
            }

        }

        @Bean
        public UserDetailsService userDetailsService(){
            return new CustomUserDetailsService();
        }

        @Bean
        public AuthenticationProvider authenticationProvider(){
            return new CustomAuthenticationProvider(userDetailsService());
        }

        @Bean
        public AuthenticationFailureHandler authenticationFailureHandler(){
            return new CustomAuthenticationFailureHandler();
        }

        @Bean
        public AuthenticationEntryPoint authenticationEntryPoint(){
            return new CustomAuthenticationEntryPoint();
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
                            .failureHandler(authenticationFailureHandler())
                    .and()
                        .exceptionHandling()
                            .authenticationEntryPoint(authenticationEntryPoint())
                    .and()
                        .logout()
                            .invalidateHttpSession(true)
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                            .logoutSuccessHandler(new SimpleUrlLogoutSuccessHandler())
                            .addLogoutHandler(new SecurityContextLogoutHandler());
        }

    }
}
