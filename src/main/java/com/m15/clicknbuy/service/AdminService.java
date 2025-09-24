package com.m15.clicknbuy.service;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.m15.clicknbuy.dto.ProductDto;
import com.m15.clicknbuy.entity.Product;

import jakarta.servlet.http.HttpSession;

public interface AdminService {

	String addProduct(ProductDto productDto, BindingResult result, HttpSession session);

	String deleteProduct(Long id, HttpSession session);

	String editProduct(Long id, ModelMap map);

	String updateProduct(Product product, MultipartFile image, HttpSession session);

}
