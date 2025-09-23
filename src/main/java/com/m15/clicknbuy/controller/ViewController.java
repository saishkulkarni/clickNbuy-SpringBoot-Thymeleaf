package com.m15.clicknbuy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.m15.clicknbuy.dto.PasswordDto;
import com.m15.clicknbuy.dto.UserDto;

import jakarta.servlet.http.HttpSession;

@Controller
public class ViewController {

	@GetMapping("/")
	public String loadHome(HttpSession session, ModelMap map) {
		manageMessage(session, map);
		return "home.html";
	}

	@GetMapping("/login")
	public String loadLogin(HttpSession session, ModelMap map) {
		manageMessage(session, map);
		return "login.html";
	}

	@GetMapping("/register")
	public String loadRegister(UserDto userDto) {
		return "register.html";
	}

	@GetMapping("/otp")
	public String loadOtop(HttpSession session, ModelMap map) {
		if (session.getAttribute("id") != null) {
			map.put("id", session.getAttribute("id"));
			session.removeAttribute("id");
		}
		manageMessage(session, map);
		return "otp.html";
	}

	@GetMapping("/forgot-password")
	public String loadForgotPassword(HttpSession session, ModelMap map) {
		manageMessage(session, map);
		return "forgot-password.html";
	}

	@GetMapping("/reset-password")
	public String loadResetPassword(PasswordDto passwordDto, HttpSession session, ModelMap map) {
		if (session.getAttribute("id") != null) {
			map.put("id", session.getAttribute("id"));
			session.removeAttribute("id");
		}
		manageMessage(session, map);
		return "reset-password.html";
	}

	public void manageMessage(HttpSession session, ModelMap map) {
		if (session.getAttribute("success") != null) {
			map.put("success", session.getAttribute("success"));
			session.removeAttribute("success");
		}
		if (session.getAttribute("error") != null) {
			map.put("error", session.getAttribute("error"));
			session.removeAttribute("error");
		}
	}
}
