package com.product_order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.product_order.dto.UserRequestDTO;
import com.product_order.dto.UserResponseDTO;
import com.product_order.entity.User;
import com.product_order.enums.Role;
import com.product_order.excception.DuplicateResourceException;
import com.product_order.excception.ResourceInUseException;
import com.product_order.excception.ResourceNotFoundException;
import com.product_order.repository.OrderRepository;
import com.product_order.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final OrderRepository orderRepository;

	public UserResponseDTO registerUser(UserRequestDTO dto) {
		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new DuplicateResourceException("Email already registered, Plz Login ");
		}
		User user = new User();
		user.setName(dto.getName());
		user.setEmail(dto.getEmail());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user.setRole(Role.USER);

		User saved = userRepository.save(user);
		return toDTO(saved);
	}

	public List<UserResponseDTO> getAllUsers() {
		return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
	}

	public UserResponseDTO updateUserRole(Long userId, Role role) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
		user.setRole(role);
		return toDTO(userRepository.save(user));
	}

	public void deleteUser(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new ResourceNotFoundException("User not found with id: " + userId);
		}

		if (!orderRepository.findByUserId(userId).isEmpty()) {
			throw new ResourceInUseException("Cannot delete user — they have existing orders.");
		}

		userRepository.deleteById(userId);
	}

	private UserResponseDTO toDTO(User user) {
		return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
	}
}