package com.m15.clicknbuy.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private double totalAmount;
    private String deliveryAddress;
    private LocalDateTime orderDate;

    private String status; // PENDING, PAID, DELIVERED, CANCELLED

    @OneToMany(mappedBy = "order", orphanRemoval = true)
    private List<OrderItem> orderItems;
}
