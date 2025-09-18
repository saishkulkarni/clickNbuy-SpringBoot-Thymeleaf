package com.m15.clicknbuy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.m15.clicknbuy.dto.UserDto;
import com.m15.clicknbuy.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/register")
	public String register(@Valid UserDto userDto, BindingResult result, ModelMap map) {
		return userService.registerUser(userDto, result, map);
	}

	@PostMapping("/confirm-otp")
	public String confirmOtp(@RequestParam Long id, @RequestParam int otp, ModelMap map) {
		return userService.confirmOtp(id, otp, map);
	}
}
