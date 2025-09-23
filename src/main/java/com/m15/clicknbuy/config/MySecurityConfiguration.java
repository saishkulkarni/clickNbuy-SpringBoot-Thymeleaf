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
				.authorizeHttpRequests(
						req -> req.requestMatchers("/", "/register", "/otp", "/resend-otp", "/forgot-password","/reset-password")
								.permitAll().requestMatchers("/user/**").hasRole("USER").requestMatchers("/admin/**")
								.hasRole("ADMIN").anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll()).build();
	}
}
