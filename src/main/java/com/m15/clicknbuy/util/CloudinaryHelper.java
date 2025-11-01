package com.m15.clicknbuy.util;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryHelper {

	@Value("${cloudinary.url}")
	String CLOUDINARY_URL;

	public String saveToCloudinary(MultipartFile image) {
		try {
			Cloudinary cloudinary = new Cloudinary(CLOUDINARY_URL);
			try {
				return (String) cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap()).get("url");
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "https://placehold.co/600x400";
		}
	}
}
