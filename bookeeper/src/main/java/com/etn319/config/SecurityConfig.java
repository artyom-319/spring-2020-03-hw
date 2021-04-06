package com.etn319.config;

import com.etn319.dao.mongo.UserMongoRepository;
import com.etn319.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeRequests().antMatchers("/**").authenticated()
                .and()
                .formLogin().loginProcessingUrl("/login").usernameParameter("username").passwordParameter("password")
                .successHandler((rq, rs, auth) -> {
                    rs.getWriter().print(String.format("{ username: %s}", ((User) auth.getPrincipal()).getUsername()));
                    rs.getWriter().flush();
                })
                .failureHandler((rq, rs, e) -> rs.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage()))
                .and()
                .logout().logoutUrl("/logout").invalidateHttpSession(true)
                .and()
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("*")
                .allowCredentials(true);
    }

    @Bean
    public UserDetailsService userDetailsService(UserMongoRepository repository) {
        return new UserDetailsServiceImpl(repository);
    }
}
