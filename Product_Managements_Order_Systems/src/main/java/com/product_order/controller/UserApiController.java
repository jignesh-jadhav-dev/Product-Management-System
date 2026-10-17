package com.product_order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.product_order.dto.AddressDTO;
import com.product_order.dto.CartItemRequestDTO;
import com.product_order.dto.CartResponseDTO;
import com.product_order.dto.OrderResponseDTO;
import com.product_order.excception.InvalidRequestException;
import com.product_order.excception.ResourceNotFoundException;
import com.product_order.repository.UserRepository;
import com.product_order.service.AddressService;
import com.product_order.service.CartService;
import com.product_order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

	private final CartService cartService;
	private final AddressService addressService;
	private final OrderService orderService;
	private final UserRepository userRepository;

	public record QuantityRequest(Integer quantity) {
	}

	public record PlaceOrderRequest(Long addressId) {
	}

	// ---------- CART ----------
	@GetMapping("/cart")
	public CartResponseDTO cart(Authentication auth) {
		return cartService.getCart(userId(auth));
	}

	@PostMapping("/cart/items")
	public CartResponseDTO addItem(@Valid @RequestBody CartItemRequestDTO dto, Authentication auth) {
		return cartService.addOrUpdateItem(userId(auth), dto);
	}

	@PutMapping("/cart/items/{id}")
	public CartResponseDTO updateItem(@PathVariable Long id, @RequestBody QuantityRequest body, Authentication auth) {
		return cartService.updateItemQuantity(userId(auth), id, body.quantity());
	}

	@DeleteMapping("/cart/items/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeItem(@PathVariable Long id, Authentication auth) {
		cartService.removeItem(userId(auth), id);
	}

	// ---------- ADDRESS ----------
	@GetMapping("/addresses")
	public List<AddressDTO> addresses(Authentication auth) {
		return addressService.getAddressesByUser(userId(auth));
	}

	@PostMapping("/addresses")
	@ResponseStatus(HttpStatus.CREATED)
	public AddressDTO addAddress(@Valid @RequestBody AddressDTO dto, Authentication auth) {
		return addressService.addAddress(userId(auth), dto);
	}

	// ---------- ORDERS ----------
	@PostMapping("/orders")
	@ResponseStatus(HttpStatus.CREATED)
	public OrderResponseDTO placeOrder(@RequestBody PlaceOrderRequest body, Authentication auth) {
		if (body.addressId() == null) {
			throw new InvalidRequestException("Please select a delivery address");
		}
		return orderService.placeOrder(userId(auth), body.addressId());
	}

	@GetMapping("/orders")
	public List<OrderResponseDTO> myOrders(Authentication auth) {
		return orderService.getOrdersByUser(userId(auth));
	}

	private Long userId(Authentication auth) {
		return userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found")).getId();
	}
}