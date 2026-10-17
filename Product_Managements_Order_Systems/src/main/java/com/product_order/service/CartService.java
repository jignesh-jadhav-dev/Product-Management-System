package com.product_order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.product_order.dto.CartItemRequestDTO;
import com.product_order.dto.CartItemViewDTO;
import com.product_order.dto.CartResponseDTO;
import com.product_order.entity.Cart;
import com.product_order.entity.CartItem;
import com.product_order.entity.Inventory;
import com.product_order.entity.Product;
import com.product_order.entity.User;
import com.product_order.excception.InsufficientStockException;
import com.product_order.excception.InvalidRequestException;
import com.product_order.excception.ResourceNotFoundException;
import com.product_order.repository.CartItemRepository;
import com.product_order.repository.CartRepository;
import com.product_order.repository.InventoryRepository;
import com.product_order.repository.ProductRepository;
import com.product_order.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final InventoryRepository inventoryRepository;

	public CartResponseDTO addOrUpdateItem(Long userId, CartItemRequestDTO dto){
		Cart cart = getOrCreateCart(userId);
		Product product = productRepository.findById(dto.getProductId())
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId()));

		Inventory inventory = inventoryRepository.findByProductId(product.getId()).orElseThrow(
				() -> new ResourceNotFoundException("Inventory not found for product: " + product.getName()));

		CartItem existingItem = cart.getCartItems().stream()
				.filter(item -> item.getProduct().getId().equals(dto.getProductId())).findFirst().orElse(null);

		if (existingItem != null) {
			int newQuantity = existingItem.getQuantity() + dto.getQuantity();

			if (inventory.getQuantity() < newQuantity) {
				throw new InsufficientStockException(
						"Only " + inventory.getQuantity() + " unit(s) available for " + product.getName());
			}

			existingItem.setQuantity(newQuantity);
			cartItemRepository.save(existingItem);
		} else {
			if (inventory.getQuantity() < dto.getQuantity()) {
				throw new InsufficientStockException(
						"Only " + inventory.getQuantity() + " unit(s) available for " + product.getName());
			}

			CartItem newItem = new CartItem();
			newItem.setProduct(product);
			newItem.setQuantity(dto.getQuantity());
			newItem.setCart(cart);
			cartItemRepository.save(newItem);
			cart.getCartItems().add(newItem);
		}
		return getCart(userId);
	}

	public void removeItem(Long userId, Long cartItemId) {
		CartItem item = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
		if (!item.getCart().getUser().getId().equals(userId)) {
			throw new ResourceNotFoundException("Cart item not found with id: " + cartItemId);
		}
		cartItemRepository.delete(item);

	}

	public CartResponseDTO updateItemQuantity(Long userId, Long cartItemId, Integer quantity) {
		if (quantity == null || quantity < 1) {
			throw new InvalidRequestException("Quantity must be at least 1");
		}

		CartItem item = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

		if (!item.getCart().getUser().getId().equals(userId)) {
			throw new ResourceNotFoundException("Cart item not found with id: " + cartItemId);
		}

		Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId()).orElseThrow(
				() -> new ResourceNotFoundException("Inventory not found for product: " + item.getProduct().getName()));
		if (inventory.getQuantity() < quantity) {
			throw new InsufficientStockException(
					"Only " + inventory.getQuantity() + " unit(s) available for " + item.getProduct().getName());
		}

		item.setQuantity(quantity);
		cartItemRepository.save(item);

		return getCart(userId);
	}

	public CartResponseDTO getCart(Long userId) {
		Cart cart = getOrCreateCart(userId);
		List<CartItemViewDTO> items = cart.getCartItems().stream()
				.map(item -> new CartItemViewDTO(item.getId(), item.getProduct().getId(), item.getProduct().getName(),
						item.getQuantity(), item.getProduct().getPrice(), inventoryRepository
								.findByProductId(item.getProduct().getId()).map(Inventory::getQuantity).orElse(0)))
				.collect(Collectors.toList());

		Double total = items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
		return new CartResponseDTO(cart.getId(), items, total);
	}

	private Cart getOrCreateCart(Long userId) {
		return cartRepository.findByUserId(userId).orElseGet(() -> {
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
			Cart newCart = new Cart();
			newCart.setUser(user);
			return cartRepository.save(newCart);
		});
	}
}