package com.m15.clicknbuy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.m15.clicknbuy.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	boolean existsByMobile(Long mobile);

	Optional<User> findByEmail(String email);
}
