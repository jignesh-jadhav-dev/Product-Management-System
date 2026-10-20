package com.product_order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.product_order.dto.CategoryDTO;
import com.product_order.dto.OrderResponseDTO;
import com.product_order.dto.UserResponseDTO;
import com.product_order.enums.Role;
import com.product_order.excception.InvalidRequestException;
import com.product_order.service.CategoryService;
import com.product_order.service.OrderService;
import com.product_order.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
public class SuperAdminApiController {

	private final CategoryService categoryService;
	private final UserService userService;
	private final OrderService orderService;

	public record RoleRequest(Role role) {
	}

	@PostMapping("/categories")
	@ResponseStatus(HttpStatus.CREATED)
	public CategoryDTO addCategory(@Valid @RequestBody CategoryDTO dto) {
		return categoryService.createCategory(dto);
	}

	@DeleteMapping("/categories/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCategory(@PathVariable Long id) {
		categoryService.deleteCategory(id);
	}

	@GetMapping("/users")
	public List<UserResponseDTO> users() {
		return userService.getAllUsers();
	}

	@PutMapping("/users/{id}/role")
	public UserResponseDTO updateRole(@PathVariable Long id, @RequestBody RoleRequest body) {
		if (body.role() == null) {
			throw new InvalidRequestException("Role is required");
		}
		return userService.updateUserRole(id, body.role());
	}

	@DeleteMapping("/users/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
	}

	@GetMapping("/orders")
	public List<OrderResponseDTO> orders() {
		return orderService.getAllOrders();
	}
}