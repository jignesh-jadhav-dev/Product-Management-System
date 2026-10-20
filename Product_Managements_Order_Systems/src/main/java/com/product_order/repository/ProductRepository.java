package com.product_order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product_order.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    List<Product> findAllByOrderByUpdatedAtDesc();

    List<Product> findByCategoryIdOrderByUpdatedAtDesc(Long categoryId);

    boolean existsByCategoryId(Long categoryId);

    boolean existsByNameIgnoreCase(String name);
    
}