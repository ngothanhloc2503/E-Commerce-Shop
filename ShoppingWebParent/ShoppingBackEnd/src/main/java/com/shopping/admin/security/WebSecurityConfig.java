package com.shopping.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new ShoppingUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService());
//        authProvider.setPasswordEncoder(passwordEncoder());
//
//        return authProvider;
//    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authenticationProvider());
//    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(new ShoppingUserDetailsService())
                .passwordEncoder(new BCryptPasswordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .requestMatchers("/states/list_by_country/**")
                    .hasAnyAuthority("Admin", "Salesperson")

                .requestMatchers("/users/**", "/settings/**", "/countries/**", "/states/**")
                    .hasAuthority("Admin")

                .requestMatchers("/categories/**", "/brands/**", "/articles/**", "/menus/**")
                    .hasAnyAuthority("Admin", "Editor")

                .requestMatchers("/products/save", "/products/edit/**", "/products/check_unique")
                    .hasAnyAuthority("Admin", "Editor", "Salesperson")

                .requestMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
                    .hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")

                .requestMatchers("/products/**").hasAnyAuthority("Admin", "Editor")

                .requestMatchers("/customers/**", "/shipping/**", "/reports/**", "/get_shipping_cost")
                    .hasAnyAuthority("Admin", "Salesperson")

                .requestMatchers("/orders", "/orders/", "/orders/page/**", "/orders/detail/**")
                    .hasAnyAuthority("Admin", "Salesperson", "Shipper")

                .requestMatchers("/orders/**", "/reports/**")
                    .hasAnyAuthority("Admin", "Salesperson")

                .requestMatchers("/orders_shipper/update/**").hasAnyAuthority("Shipper")
                .requestMatchers("/reviews/**").hasAnyAuthority("Admin", "Assistant")

                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .usernameParameter("email")
                    .permitAll()
                .and().logout().permitAll()
                .and()
                    .rememberMe()
                    .key("E_Commerce_Shopping")
                    .tokenValiditySeconds(30 * 24 * 60 * 60); // 1 month
            http.headers().frameOptions().sameOrigin();
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
    }
}
