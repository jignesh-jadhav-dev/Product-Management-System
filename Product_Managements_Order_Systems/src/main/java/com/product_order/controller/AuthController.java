package com.product_order.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.product_order.dto.UserRequestDTO;
import com.product_order.dto.UserResponseDTO;
import com.product_order.entity.User;
import com.product_order.excception.ResourceNotFoundException;
import com.product_order.repository.UserRepository;
import com.product_order.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;
	private final UserRepository userRepository;

	// React ye token fetch karke har POST/PUT/PATCH/DELETE ke header mein bhejta
	// hai
	@GetMapping("/csrf")
	public Map<String, String> csrf(CsrfToken token) {
		return Map.of("headerName", token.getHeaderName(), "token", token.getToken());
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponseDTO register(@Valid @RequestBody UserRequestDTO dto) {
		return userService.registerUser(dto);
	}

	// Logged-in user ki info (React isse role pata karta hai). Login nahi to 401.
	@GetMapping("/me")
	public UserResponseDTO me(Authentication authentication) {
		User u = userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return new UserResponseDTO(u.getId(), u.getName(), u.getEmail(), u.getRole());
	}
}