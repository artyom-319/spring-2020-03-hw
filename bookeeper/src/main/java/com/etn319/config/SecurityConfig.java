package com.etn319.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()).and()
                .authorizeRequests().antMatchers("/**").permitAll()
                .and()

        .httpBasic()
//                .formLogin().loginProcessingUrl("/login").usernameParameter("username").passwordParameter("password")
//                .successHandler((rq, rs, auth) -> {
//                    rs.getWriter().print(String.format("{ username: %s}", ((User) auth.getPrincipal()).getUsername()));
//                    rs.getWriter().flush();
//                })
//                .failureHandler((rq, rs, e) -> rs.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage()))
//                .and()
//                .rememberMe().alwaysRemember(true).key("secret")

        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password(passwordEncoder().encode("password"))
                .roles("ADMIN");
    }
}
