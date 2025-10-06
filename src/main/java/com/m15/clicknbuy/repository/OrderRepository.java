package com.m15.clicknbuy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.m15.clicknbuy.entity.Order;
import com.m15.clicknbuy.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    Order findByRazorpayOrderId(String razorpayOrderId);
}
