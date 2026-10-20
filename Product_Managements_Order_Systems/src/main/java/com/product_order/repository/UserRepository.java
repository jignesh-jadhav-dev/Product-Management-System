package com.product_order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product_order.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}
