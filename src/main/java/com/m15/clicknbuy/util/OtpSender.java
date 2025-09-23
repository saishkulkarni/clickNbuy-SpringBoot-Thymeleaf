package com.m15.clicknbuy.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

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
			System.err.println("The OTP is : " + otp);
		}
	}

	public void sendOtpThruMobile(Long mobile, int otp, String name) {
		try {
			Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"), System.getenv("TWILIO_AUTH_TOKEN"));

			Message.creator(new PhoneNumber("+91" + mobile), new PhoneNumber(System.getenv("TWILIO_MOBILE")),
					"Hello " + name + " Thanks for creating account your OTP is " + otp).create();
		} catch (Exception e) {
			System.err.println("The OTP is : " + otp);
		}
	}

}
