package com.m15.clicknbuy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.m15.clicknbuy.dto.PasswordDto;
import com.m15.clicknbuy.dto.UserDto;
import com.m15.clicknbuy.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/register")
	public String register(@Valid UserDto userDto, BindingResult result, HttpSession session) {
		return userService.registerUser(userDto, result, session);
	}

	@PostMapping("/otp")
	public String confirmOtp(@RequestParam Long id, @RequestParam int otp, HttpSession session) {
		return userService.confirmOtp(id, otp, session);
	}

	@GetMapping("/resend-otp")
	public String resendOtp(@RequestParam Long id, HttpSession session) {
		return userService.resendOtp(id, session);
	}

	@PostMapping("/forgot-password")
	public String forgotPassword(@RequestParam String email, HttpSession session) {
		return userService.forgotPassword(email, session);
	}

	@PostMapping("/reset-password")
	public String resetPassword(@Valid PasswordDto passwordDto, @RequestParam Long id, BindingResult result,
			HttpSession session, ModelMap map) {
		return userService.resetPassword(passwordDto, result, session, id, map);
	}
}
