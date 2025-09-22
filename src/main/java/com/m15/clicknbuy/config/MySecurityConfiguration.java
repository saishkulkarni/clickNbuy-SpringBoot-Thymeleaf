package com.m15.clicknbuy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MySecurityConfiguration {

	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain chain(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(req -> req
						.requestMatchers("/", "/register", "/confirm-otp", "/error", "/error/**", "/css/**",
								"/favicon.ico", "/js/**", "/images/**", "/webjars/**")
						.permitAll().requestMatchers("/home").hasRole("USER"))
				.httpBasic(httpBasic -> httpBasic.disable())
				.formLogin(form -> form.loginPage("/login").successForwardUrl("/home").permitAll()).build();
	}
}
