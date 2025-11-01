package com.m15.clicknbuy.service.impl;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.m15.clicknbuy.dao.UserDao;
import com.m15.clicknbuy.dto.PasswordDto;
import com.m15.clicknbuy.dto.UserDto;
import com.m15.clicknbuy.entity.CartItem;
import com.m15.clicknbuy.entity.Order;
import com.m15.clicknbuy.entity.OrderItem;
import com.m15.clicknbuy.entity.Product;
import com.m15.clicknbuy.entity.User;
import com.m15.clicknbuy.repository.CartItemRepository;
import com.m15.clicknbuy.repository.OrderItemRepository;
import com.m15.clicknbuy.repository.OrderRepository;
import com.m15.clicknbuy.repository.ProductRepository;
import com.m15.clicknbuy.service.UserService;
import com.m15.clicknbuy.util.OtpSender;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	CartItemRepository itemRepository;

	@Value("${razorpay.id}")
	private String razorpayKeyId;

	@Value("${razorpay.secret}")
	private String razorpayKeySecret;

	@Autowired
	OtpSender otpSender;

	@Autowired
	OrderItemRepository orderItemRepository;

	@Autowired
	OrderRepository orderRepository;

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

	@Override
	public String addToCart(Long id, HttpSession session, Principal principal) {
		Product product = productRepository.findById(id).orElseThrow();
		if (product.getStock() > 0) {
			String email = principal.getName();
			User user = userDao.findByEmail(email).orElseThrow();

			List<CartItem> items = itemRepository.findByUser(user);
			if (items.isEmpty()) {
				CartItem item = new CartItem();
				item.setUser(user);
				item.setProduct(product);
				item.setQuantity(1);
				itemRepository.save(item);
			} else {
				boolean flag = true;
				for (CartItem item : items) {
					if (item.getProduct().getId().equals(product.getId())) {
						item.setQuantity(item.getQuantity() + 1);
						itemRepository.save(item);
						flag = false;
						break;
					}
				}
				if (flag) {
					CartItem item = new CartItem();
					item.setUser(user);
					item.setProduct(product);
					item.setQuantity(1);
					itemRepository.save(item);
				}
			}
			product.setStock(product.getStock() - 1);
			productRepository.save(product);
			session.setAttribute("success", "Product Added to Cart");
			return "redirect:/user/cart";
		} else {
			session.setAttribute("error", "OUT OF STOCK");
			return "redirect:/";
		}
	}

	@Override
	public String viewCart(HttpSession session, Principal principal, ModelMap map) {
		String email = principal.getName();
		User user = userDao.findByEmail(email).orElseThrow();
		List<CartItem> items = itemRepository.findByUser(user);
		if (items.isEmpty()) {
			session.setAttribute("error", "No Products in Cart");
			return "redirect:/";
		} else {
			if (session.getAttribute("success") != null) {
				map.put("success", session.getAttribute("success"));
				session.removeAttribute("success");
			}
			if (session.getAttribute("error") != null) {
				map.put("error", session.getAttribute("error"));
				session.removeAttribute("error");
			}
			map.put("items", items);
			double total = items.stream().mapToDouble(x -> x.getProduct().getPrice() * x.getQuantity()).sum();
			map.put("total", Math.round(total * 100.0) / 100.0); // Round to 2 decimal places
			return "cart.html";
		}
	}

	@Override
	public String increase(HttpSession session, Long id, ModelMap map) {
		CartItem item = itemRepository.findById(id).orElseThrow();
		Product product = item.getProduct();
		if (product.getStock() > 0) {
			item.setQuantity(item.getQuantity() + 1);
			itemRepository.save(item);
			product.setStock(product.getStock() - 1);
			productRepository.save(product);
			session.setAttribute("success", "Item Increased Success");
			return "redirect:/user/cart";
		} else {
			session.setAttribute("error", "Not Enough Stock");
			return "redirect:/user/cart";
		}
	}

	@Override
	public String decrease(HttpSession session, Long id, ModelMap map) {
		CartItem item = itemRepository.findById(id).orElseThrow();
		Product product = item.getProduct();
		if (item.getQuantity() > 1) {
			item.setQuantity(item.getQuantity() - 1);
			itemRepository.save(item);
			product.setStock(product.getStock() + 1);
			productRepository.save(product);
			session.setAttribute("success", "Item Decreased Success");
			return "redirect:/user/cart";
		} else {
			itemRepository.delete(item);
			session.setAttribute("success", "Item Decreased Success");
			return "redirect:/user/cart";
		}
	}

	@Override
	public String checkout(HttpSession session, Principal principal, ModelMap map) {
		String email = principal.getName();
		User user = userDao.findByEmail(email).orElseThrow();
		List<CartItem> items = itemRepository.findByUser(user);
		if (items.isEmpty()) {
			session.setAttribute("error", "No Products in Cart");
			return "redirect:/";
		} else {
			try {
				double total = items.stream().mapToDouble(x -> x.getProduct().getPrice() * x.getQuantity()).sum();
				double roundedTotal = Math.round(total * 100.0) / 100.0; // Round to 2 decimal places

				RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
				JSONObject orderRequest = new JSONObject();
				orderRequest.put("amount", (int) (roundedTotal * 100));
				orderRequest.put("currency", "INR");
				orderRequest.put("receipt", "order_" + System.currentTimeMillis());

				com.razorpay.Order order = razorpay.orders.create(orderRequest);

				map.put("razorpayOrderId", order.get("id"));
				map.put("razorpayKeyId", razorpayKeyId);
				map.put("amount", roundedTotal);
				map.put("currency", "INR");
				map.put("userEmail", user.getEmail());
				map.put("userContact", user.getMobile());
				map.put("userName", user.getName());
				map.put("items", items);
				map.put("user", user);
				map.put("total", roundedTotal);

				return "checkout.html";
			} catch (RazorpayException e) {
				session.setAttribute("error", "Payment initialization failed. Please try again.");
				return "redirect:/user/cart";
			}
		}
	}

	@Override
	public String paymentSuccess(String paymentId, String orderId, String signature, String address,
			Principal principal,
			HttpSession session, ModelMap map) {
		try {
			String data = orderId + "|" + paymentId;
			boolean isValidSignature = Utils.verifySignature(data, signature, razorpayKeySecret);

			System.out.println("----------2--------------");
			if (!isValidSignature) {
				map.put("error", "Payment verification failed!");
				return "redirect:/checkout";
			}

			System.out.println("----------3--------------");
			// Get the current user
			String email = principal.getName();
			User user = userDao.findByEmail(email).orElseThrow();
			if (user == null) {
				return "redirect:/login";
			}

			// Create a new order
			Order order = new Order();
			order.setUser(user);
			order.setRazorpayOrderId(orderId);
			order.setRazorpayPaymentId(paymentId);
			order.setRazorpaySignature(signature);
			order.setOrderDate(LocalDateTime.now());
			order.setStatus("PAID");
			order.setDeliveryAddress(address);

			// Get cart items and calculate total
			List<CartItem> cartItems = itemRepository.findByUser(user);
			// Create order items
			List<OrderItem> orderItems = cartItems.stream()
				.map(cartItem -> {
					OrderItem orderItem = new OrderItem();
					orderItem.setOrder(order);
					orderItem.setProduct(cartItem.getProduct());
					orderItem.setQuantity(cartItem.getQuantity());
					orderItem.setPrice(cartItem.getProduct().getPrice());
					return orderItem;
				})
				.toList();
			
			// Calculate total amount
			double totalAmount = cartItems.stream()
				.mapToDouble(cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPrice())
				.sum();
			
			// Set the total amount and save the order
			order.setTotalAmount(totalAmount);
			orderRepository.save(order);
			
			// Save all order items
			orderItemRepository.saveAll(orderItems);

			// Clear the cart
			itemRepository.deleteAll(cartItems);

			map.put("success", "Order placed successfully!");
			return "redirect:/user/orders";

		} catch (RazorpayException e) {
			map.put("error", "Payment processing failed: " + e.getMessage());
			return "redirect:/checkout";
		}
	}

	@Override
	public String viewOrders(Principal principal, HttpSession session, ModelMap map) {
		if (principal == null) {
			return "redirect:/login";
		}

		User user = userDao.findByEmail(principal.getName()).orElseThrow();
		List<Order> orders = orderRepository.findByUser(user);
		map.put("orders", orders);
		return "orders";
	}

}
