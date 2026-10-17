package com.product_order.dto;

import com.product_order.enums.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
	private Long id;
	private String name;
	private String description;
	private Double price;
	private ProductStatus status;
	private String categoryName;
	private Integer availableQuantity;
}