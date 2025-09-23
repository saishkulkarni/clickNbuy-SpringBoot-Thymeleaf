package com.m15.clicknbuy.service;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.m15.clicknbuy.dto.PasswordDto;
import com.m15.clicknbuy.dto.UserDto;

import jakarta.servlet.http.HttpSession;

public interface UserService {
	String registerUser(UserDto userDto, BindingResult result, HttpSession session);

	String confirmOtp(Long id, int otp, HttpSession session);

	String resendOtp(Long id, HttpSession session);

	String forgotPassword(String email, HttpSession session);

	String resetPassword(PasswordDto passwordDto, BindingResult result, HttpSession session, Long id,ModelMap map);
}
