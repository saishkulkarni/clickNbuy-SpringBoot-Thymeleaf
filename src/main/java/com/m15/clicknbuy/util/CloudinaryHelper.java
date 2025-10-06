package com.m15.clicknbuy.util;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryHelper {

	@Value("${CLOUDINARY_URL}")
	String CLOUDINARY_URL;

	public String saveToCloudinary(MultipartFile image) {
		Cloudinary cloudinary = new Cloudinary(CLOUDINARY_URL);
		try {
			return (String) cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap()).get("url");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
