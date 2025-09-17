package com.m15.clicknbuy.config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MySecurityConfiguration {
	@Bean
	SecurityFilterChain chain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(req -> req.requestMatchers("/", "/register", "/error").permitAll())
				.httpBasic(httpBasic -> httpBasic.disable()).formLogin(form -> form.loginPage("/login").permitAll())
				.build();
	}
}
