package com.m15.clicknbuy.service.impl;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.m15.clicknbuy.dao.UserDao;
import com.m15.clicknbuy.dto.UserDto;
import com.m15.clicknbuy.entity.User;
import com.m15.clicknbuy.service.UserService;
import com.m15.clicknbuy.util.OtpSender;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	OtpSender otpSender;

	@Override
	public String registerUser(UserDto userDto, BindingResult result, ModelMap map) {
		if (!userDto.getPassword().equals(userDto.getConfirmPassword()))
			result.rejectValue("confirmPassword", "error.confirmPassword",
					"* Password and Confirm Password Shoulb be Same");
		if (userDao.emailExists(userDto.getEmail()))
			result.rejectValue("email", "error.email", "* Email Should be Unique");
		if (userDao.mobileExists(userDto.getMobile()))
			result.rejectValue("mobile", "error.mobile", "* Mobile Number Should be Unique");

		if (result.hasErrors())
			return "register.html";
		else {
			int otp = new Random().nextInt(100000, 1000000);
			otpSender.sendOtpThruEmail(userDto.getEmail(), otp, userDto.getName());
			otpSender.sendOtpThruMobile(userDto.getMobile(), otp, userDto.getName());
			User user = new User(null, userDto.getName(), userDto.getEmail(), encoder.encode(userDto.getPassword()),
					userDto.getMobile(), userDto.getGender(), otp, false);
			userDao.save(user);
			map.put("id", user.getId());
			return "otp.html";
		}
	}

	@Override
	public String confirmOtp(Long id, int otp, ModelMap map) {
		User user = userDao.findById(id);
		if (user.getOtp() == otp) {
			user.setVerified(true);
			user.setOtp(0);
			userDao.save(user);
			map.put("success", "Account Created Success");
			return "login.html";
		} else {
			map.put("id", user.getId());
			map.put("failure", "Invalid OTP , Try Again");
			return "otp.html";
		}
	}

}
