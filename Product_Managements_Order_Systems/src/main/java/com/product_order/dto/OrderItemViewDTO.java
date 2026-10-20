package com.product_order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemViewDTO {
	private String productName;
	private Integer quantity;
	private Double price;
}