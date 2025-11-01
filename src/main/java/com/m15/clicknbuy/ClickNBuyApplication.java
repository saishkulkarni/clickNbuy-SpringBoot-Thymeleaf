package com.m15.clicknbuy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ClickNBuyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClickNBuyApplication.class, args);
	}

}