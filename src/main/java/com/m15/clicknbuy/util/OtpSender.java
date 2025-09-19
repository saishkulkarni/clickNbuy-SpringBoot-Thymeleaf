package com.m15.clicknbuy.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Service
public class OtpSender {

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	TemplateEngine templateEngine;

	public void sendOtpThruEmail(String email, int otp, String name) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom("noreply@clicknbuy.com", "Clicknbuy");
			helper.setTo(email);
			helper.setSubject("Otp for Creating account with ClickNBuy");
			Context context = new Context();
			context.setVariable("name", name);
			context.setVariable("otp", otp);
			String emailMessage = templateEngine.process("email-template.html", context);
			helper.setText(emailMessage, true);
			mailSender.send(message);
		} catch (Exception e) {
		}
	}

	public void sendOtpThruMobile(Long mobile, int otp, String name) {
		System.out.println("Otp Sent to Mobile : " + mobile + " , OTP is : " + otp);
	}

}
