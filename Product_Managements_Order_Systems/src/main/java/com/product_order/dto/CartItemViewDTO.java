package com.product_order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemViewDTO {
	private Long cartItemId;
	private Long productId;
	private String productName;
	private Integer quantity;
	private Double price;
	private Integer availableQuantity;
}