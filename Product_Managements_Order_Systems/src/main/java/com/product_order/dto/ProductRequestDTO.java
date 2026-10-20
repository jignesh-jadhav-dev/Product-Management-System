package com.product_order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequestDTO {

	@NotBlank(message = "Product name is required")
	private String name;

//    @Size(min = 10, max = 454)
	private String description;

	@NotNull(message = "Price is required")
	@Positive(message = "Price must be positive")
	private Double price;

	@NotNull(message = "Category id is required")
	private Long categoryId;

	@NotNull(message = "Quantity is required")
	@Min(value = 0, message = "Quantity cannot be negative")
	private Integer quantity;

}