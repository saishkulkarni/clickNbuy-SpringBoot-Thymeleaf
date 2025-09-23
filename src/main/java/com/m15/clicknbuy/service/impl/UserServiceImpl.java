package com.m15.clicknbuy.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.m15.clicknbuy.dao.UserDao;
import com.m15.clicknbuy.dto.PasswordDto;
import com.m15.clicknbuy.dto.UserDto;
import com.m15.clicknbuy.entity.User;
import com.m15.clicknbuy.service.UserService;
import com.m15.clicknbuy.util.OtpSender;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	OtpSender otpSender;

	@Override
	public String registerUser(UserDto userDto, BindingResult result, HttpSession session) {
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
					userDto.getMobile(), userDto.getGender(), otp, false, "ROLE_USER", null);
			userDao.save(user);
			session.setAttribute("success", "Otp Sent Success");
			session.setAttribute("id", user.getId());
			return "redirect:/otp";
		}
	}

	@Override
	public String confirmOtp(Long id, int otp, HttpSession session) {
		User user = userDao.findById(id);
		if (user.getOtp() == otp) {
			if (user.getCreatedTime().plusMinutes(5).isAfter(LocalDateTime.now())) {
				user.setVerified(true);
				user.setOtp(0);
				userDao.save(user);
				session.setAttribute("success", "Account Created Success");
				return "redirect:/login";
			} else {
				session.setAttribute("id", user.getId());
				session.setAttribute("error", "Otp Expired, try resending");
				return "redirect:/otp";
			}
		} else {
			session.setAttribute("id", user.getId());
			session.setAttribute("error", "Invalid OTP , Try Again");
			return "redirect:/otp";
		}
	}

	@Override
	public String resendOtp(Long id, HttpSession session) {
		User user = userDao.findById(id);
		int otp = new Random().nextInt(100000, 1000000);
		user.setOtp(otp);
		otpSender.sendOtpThruEmail(user.getEmail(), otp, user.getName());
		otpSender.sendOtpThruMobile(user.getMobile(), otp, user.getName());
		user.setCreatedTime(LocalDateTime.now());
		userDao.save(user);
		session.setAttribute("success", "Otp Re-sent Success");
		session.setAttribute("id", user.getId());
		return "redirect:/otp";
	}

	@Override
	public String forgotPassword(String email, HttpSession session) {
		Optional<User> opUser = userDao.findByEmail(email);
		if (opUser.isEmpty()) {
			session.setAttribute("error", "No Account with entered Email");
			return "redirect:/forgot-password";
		} else {
			User user = opUser.get();
			int otp = new Random().nextInt(100000, 1000000);
			user.setOtp(otp);
			otpSender.sendOtpThruEmail(user.getEmail(), otp, user.getName());
			user.setCreatedTime(LocalDateTime.now());
			userDao.save(user);
			session.setAttribute("success", "Otp Sent Success");
			session.setAttribute("id", user.getId());
			return "redirect:/reset-password";
		}
	}

	@Override
	public String resetPassword(@Valid PasswordDto passwordDto, BindingResult result, HttpSession session, Long id,
			ModelMap map) {
		if (!passwordDto.getPassword().equals(passwordDto.getConfirmPassword()))
			result.rejectValue("password", "error.password", "* Password and Confirm Password Should be matching");

		if (result.hasErrors()) {
			map.put("id", id);
			return "/reset-password";
		} else {
			User user = userDao.findById(id);
			if (user.getOtp() == passwordDto.getOtp()) {
				if (user.getCreatedTime().plusMinutes(5).isAfter(LocalDateTime.now())) {
					user.setPassword(encoder.encode(passwordDto.getPassword()));
					user.setOtp(0);
					user.setVerified(true);
					userDao.save(user);
					session.setAttribute("success", "Password Reset Success");
					return "redirect:/login";
				} else {
					session.setAttribute("id", user.getId());
					session.setAttribute("error", "Otp expired retry");
					session.setAttribute("id", user.getId());
					return "redirect:/reset-password";
				}
			} else {
				session.setAttribute("id", user.getId());
				session.setAttribute("error", "Invalid OTP Try Again");
				session.setAttribute("id", user.getId());
				return "redirect:/reset-password";
			}
		}
	}

}
