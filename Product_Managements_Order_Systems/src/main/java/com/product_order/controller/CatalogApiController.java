package com.product_order.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.product_order.dto.CategoryDTO;
import com.product_order.dto.ProductResponseDTO;
import com.product_order.service.CategoryService;
import com.product_order.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CatalogApiController {

	private final ProductService productService;
	private final CategoryService categoryService;

	@GetMapping("/api/categories")
	public List<CategoryDTO> categories() {
		return categoryService.getAllCategories();
	}

	@GetMapping("/api/user/products")
	public List<ProductResponseDTO> products(@RequestParam(required = false) Long categoryId) {
		return categoryId != null ? productService.getProductsByCategory(categoryId) : productService.getAllProducts();
	}
}