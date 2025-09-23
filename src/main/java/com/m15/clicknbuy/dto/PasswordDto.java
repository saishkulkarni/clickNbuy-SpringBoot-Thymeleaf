package com.m15.clicknbuy.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDto {
	@Pattern(regexp = "^.*(?=.{8,250})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "* Confirm Password must contain one uppercase, lowercase, number and special charecter and 8 charecters atleast")
	private String password;
	@Pattern(regexp = "^.*(?=.{8,250})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "* Confirm Password must contain one uppercase, lowercase, number and special charecter and 8 charecters atleast")
	private String confirmPassword;
	private Integer otp;
}
