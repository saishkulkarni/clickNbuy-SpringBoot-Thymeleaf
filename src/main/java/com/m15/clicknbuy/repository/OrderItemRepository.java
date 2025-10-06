package com.m15.clicknbuy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.m15.clicknbuy.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
