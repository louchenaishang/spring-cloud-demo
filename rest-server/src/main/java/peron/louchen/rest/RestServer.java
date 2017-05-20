package peron.louchen.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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


        @Bean
        public UserDetailsService userDetailsService(){
            return new CustomUserDetailsService();
        }

        @Bean
        public AuthenticationProvider authenticationProvider(){
            return new CustomAuthenticationProvider(userDetailsService());
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
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                        .antMatchers("/user/**").hasRole("USER")
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/anonymous/**").anonymous()
                        .anyRequest().authenticated()
                    .and()
                        .formLogin()
                    .and()
                        .httpBasic()
                    .and()
                    .logout()
                        .invalidateHttpSession(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessHandler(new SimpleUrlLogoutSuccessHandler())
                        .addLogoutHandler(new SecurityContextLogoutHandler());
        }

    }
}
