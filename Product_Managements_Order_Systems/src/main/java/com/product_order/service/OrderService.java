package com.product_order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.product_order.dto.OrderItemViewDTO;
import com.product_order.dto.OrderResponseDTO;
import com.product_order.entity.Address;
import com.product_order.entity.Cart;
import com.product_order.entity.CartItem;
import com.product_order.entity.Inventory;
import com.product_order.entity.Order;
import com.product_order.entity.OrderItem;
import com.product_order.entity.User;
import com.product_order.excception.InsufficientStockException;
import com.product_order.excception.ResourceNotFoundException;
import com.product_order.repository.AddressRepository;
import com.product_order.repository.CartRepository;
import com.product_order.repository.InventoryRepository;
import com.product_order.repository.OrderRepository;
import com.product_order.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final CartRepository cartRepository;
	private final InventoryRepository inventoryRepository;
	private final AddressRepository addressRepository;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;

	@Transactional
	public OrderResponseDTO placeOrder(Long userId, Long addressId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		Cart cart = cartRepository.findByUserId(userId).orElseThrow(
				() -> new ResourceNotFoundException("Cart not found for this user. Add items to cart first."));

		if (cart.getCartItems().isEmpty()) {
			throw new ResourceNotFoundException("Cart is empty. Add items before checkout.");
		}

		Address address = addressRepository.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

		for (CartItem item : cart.getCartItems()) {
			Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Inventory not found for product: " + item.getProduct().getName()));
			if (inventory.getQuantity() < item.getQuantity()) {
				throw new InsufficientStockException("Insufficient stock for product: " + item.getProduct().getName());
			}
		}

		Order order = new Order();
		order.setUser(user);
		order.setAddress(address);

		double total = 0;
		for (CartItem item : cart.getCartItems()) {
			Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId()).get();
			inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
			inventoryRepository.save(inventory);

			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(item.getProduct());
			orderItem.setQuantity(item.getQuantity());
			orderItem.setPrice(item.getProduct().getPrice());
			orderItem.setOrder(order);
			order.getOrderItems().add(orderItem);

			total += item.getProduct().getPrice() * item.getQuantity();
		}
		order.setTotalAmount(total);
		Order savedOrder = orderRepository.save(order);

		cart.getCartItems().clear();
		cartRepository.save(cart);

		return toDTO(savedOrder);
	}

	public List<OrderResponseDTO> getOrdersByUser(Long userId) {
		return orderRepository.findByUserId(userId).stream().map(this::toDTO).collect(Collectors.toList());
	}

	public List<OrderResponseDTO> getAllOrders() {
		return orderRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
	}

	private OrderResponseDTO toDTO(Order order) {
		List<OrderItemViewDTO> items = order.getOrderItems().stream()
				.map(oi -> new OrderItemViewDTO(oi.getProduct().getName(), oi.getQuantity(), oi.getPrice()))
				.collect(Collectors.toList());
		return new OrderResponseDTO(order.getId(), order.getUser().getEmail(), order.getTotalAmount(),
				order.getOrderDate(), items);
	}
}