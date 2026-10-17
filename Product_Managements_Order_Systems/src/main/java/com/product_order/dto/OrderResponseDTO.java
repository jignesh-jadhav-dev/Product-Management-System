package com.product_order.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
	private Long orderId;
	private String userEmail;
	private Double totalAmount;
	private LocalDateTime orderDate;
	private List<OrderItemViewDTO> items;
}