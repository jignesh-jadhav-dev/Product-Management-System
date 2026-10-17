package com.product_order.dto;

import com.product_order.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
	private Long id;
	private String name;
	private String email;
	private Role role;
}