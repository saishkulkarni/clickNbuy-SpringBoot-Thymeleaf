package com.m15.clicknbuy.util;

import org.springframework.stereotype.Service;

@Service
public class OtpSender {
	public void sendOtpThruEmail(String email, int otp, String name) {
		System.out.println("Otp Sent to Email : " + email + " , OTP is : " + otp);
	}

	public void sendOtpThruMobile(Long mobile, int otp, String name) {
		System.out.println("Otp Sent to Mobile : " + mobile + " , OTP is : " + otp);
	}

}
