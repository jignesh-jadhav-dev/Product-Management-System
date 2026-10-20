package com.product_order.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
	private Long cartId;
	private List<CartItemViewDTO> items;
	private Double totalAmount;
}