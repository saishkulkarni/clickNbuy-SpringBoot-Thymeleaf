package com.m15.clicknbuy.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductDto {
	@Size(min = 3, max = 50, message = "* Name should be 3~50 charecters")
	private String name;
	@Min(value = 49, message = "* Product Price Should be atleast 49 Rs.")
	@Max(value = 100000, message = "* Product Price Should be atmost 100000 Rs.")
	@NotNull(message = "* Price is Required")
	private Double price;
	@Min(value = 1, message = "* Product Stock Should be atleast 1")
	@Max(value = 50, message = "* Product Stock Should be atmost 50")
	@NotNull(message = "* Stock is Required")
	private Integer stock;
	@Size(min = 20, max = 250, message = "* Description should be 20~250 charecters")
	private String description;
	@NotNull(message = "* Image is Required")
	private MultipartFile image;
	@NotBlank(message = "* Category is Required")
	private String category;
}
