package com.product_order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.product_order.dto.ProductRequestDTO;
import com.product_order.dto.ProductResponseDTO;
import com.product_order.enums.ProductStatus;
import com.product_order.excception.InvalidRequestException;
import com.product_order.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminApiController {

	private final ProductService productService;

	public record StatusRequest(ProductStatus status) {
	}

	public record PriceRequest(Double price) {
	}

	public record QuantityRequest(Integer quantity) {
	}

	@GetMapping
	public List<ProductResponseDTO> list() {
		return productService.getAllProductsForAdmin();
	}

	// Edit form prefill ke liye
	@GetMapping("/{id}")
	public ProductRequestDTO get(@PathVariable Long id) {
		return productService.getProductRequestForEdit(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductResponseDTO add(@Valid @RequestBody ProductRequestDTO dto) {
		return productService.addProduct(dto);
	}

	@PutMapping("/{id}")
	public ProductResponseDTO update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
		return productService.updateProduct(id, dto);
	}

	@PatchMapping("/{id}/status")
	public ProductResponseDTO status(@PathVariable Long id, @RequestBody StatusRequest body) {
		if (body.status() == null) {
			throw new InvalidRequestException("Status is required");
		}
		return productService.toggleStatus(id, body.status());
	}

	@PatchMapping("/{id}/price")
	public ProductResponseDTO price(@PathVariable Long id, @RequestBody PriceRequest body) {
		return productService.updatePrice(id, body.price());
	}

	@PatchMapping("/{id}/inventory")
	public ProductResponseDTO inventory(@PathVariable Long id, @RequestBody QuantityRequest body) {
		return productService.updateInventory(id, body.quantity());
	}
}