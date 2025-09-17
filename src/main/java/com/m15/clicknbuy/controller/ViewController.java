package com.m15.clicknbuy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

	@GetMapping("/")
	public String loadHome() {
		return "home.html";
	}

	@GetMapping("/login")
	public String loadLogin() {
		return "login.html";
	}

	@GetMapping("/register")
	public String loadRegister() {
		return "register.html";
	}
}
