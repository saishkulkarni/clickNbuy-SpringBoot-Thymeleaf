package com.m15.clicknbuy.service;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.m15.clicknbuy.dto.UserDto;

public interface UserService {
	String registerUser(UserDto userDto, BindingResult result, ModelMap map);

	String confirmOtp(Long id, int otp, ModelMap map);
}
