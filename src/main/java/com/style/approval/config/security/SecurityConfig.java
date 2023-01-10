package com.style.approval.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final LoginFailHandler loginFailHandler;

    private static final String[] AUTH_WHITELIST = {
            "/images/**",
            "/js/**",
            "/css/**",
            "/main"
    };

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web ->
            web.ignoring()
                    .antMatchers(AUTH_WHITELIST);

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
                .authorizeRequests()
                    .antMatchers("/auth/**").permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                        .loginPage("/auth/login/page")
                        .loginProcessingUrl("/auth/login")
                        .failureHandler(loginFailHandler)
                        .defaultSuccessUrl("/main", true)
                .and()
                    .logout()
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login/page")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true);

        return http.build();
    }
}
