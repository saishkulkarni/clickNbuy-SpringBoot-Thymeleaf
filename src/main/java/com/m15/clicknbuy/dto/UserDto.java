package com.m15.clicknbuy.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
	@Size(min = 3, max = 20, message = "* Name should be 3~20 charecters")
	private String name;
	@Email(message = "* Email should be Proper")
	@NotBlank(message = "* Email is Required")
	private String email;
	@Pattern(regexp = "^.*(?=.{8,250})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "* Password must contain one uppercase, lowercase, number and special charecter and 8 charecters atleast")
	private String password;
	@Pattern(regexp = "^.*(?=.{8,250})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "* Confirm Password must contain one uppercase, lowercase, number and special charecter and 8 charecters atleast")
	private String confirmPassword;
	@DecimalMin(value = "6000000000", message = "* Mobile Number should be Proper")
	@DecimalMax(value = "9999999999", message = "* Mobile Number should be Proper")
	@NotNull(message = "* Mobile Number is Required")
	private Long mobile;
	@NotNull(message = "* Gender is Required")
	private String gender;
	@AssertTrue(message = "* Terms should be Checked")
	private boolean terms;
}
