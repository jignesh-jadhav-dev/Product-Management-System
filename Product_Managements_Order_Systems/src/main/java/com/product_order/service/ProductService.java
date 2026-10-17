package com.product_order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.product_order.dto.ProductRequestDTO;
import com.product_order.dto.ProductResponseDTO;
import com.product_order.entity.Category;
import com.product_order.entity.Inventory;
import com.product_order.entity.Product;
import com.product_order.enums.ProductStatus;
import com.product_order.excception.DuplicateResourceException;
import com.product_order.excception.InvalidRequestException;
import com.product_order.excception.ResourceNotFoundException;
import com.product_order.repository.CategoryRepository;
import com.product_order.repository.InventoryRepository;
import com.product_order.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final InventoryRepository inventoryRepository;

	public ProductResponseDTO addProduct(ProductRequestDTO dto) {
		if (productRepository.existsByNameIgnoreCase(dto.getName())) {
			throw new DuplicateResourceException("Product already exists with name: " + dto.getName());
		}

		Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

		Product product = new Product();
		product.setName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());
		product.setCategory(category);
		product.setStatus(ProductStatus.ENABLED);
		Product savedProduct = productRepository.save(product);

		Inventory inventory = new Inventory();
		inventory.setProduct(savedProduct);
		inventory.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 0);
		inventoryRepository.save(inventory);

		return toDTO(savedProduct, inventory.getQuantity());
	}

	public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO dto) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

		if (!product.getName().equalsIgnoreCase(dto.getName())
				&& productRepository.existsByNameIgnoreCase(dto.getName())) {
			throw new DuplicateResourceException("Product already exists with name: " + dto.getName());
		}

		if (dto.getCategoryId() != null) {
			Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(
					() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
			product.setCategory(category);
		}
		product.setName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());
		Product updated = productRepository.save(product);

		Inventory inventory = inventoryRepository.findByProductId(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));
		if (dto.getQuantity() != null) {
			inventory.setQuantity(dto.getQuantity());
			inventoryRepository.save(inventory);
		}

		return toDTO(updated, inventory.getQuantity());
	}

	public ProductRequestDTO getProductRequestForEdit(Long productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
		Integer qty = inventoryRepository.findByProductId(productId).map(Inventory::getQuantity).orElse(0);

		ProductRequestDTO dto = new ProductRequestDTO();
		dto.setName(product.getName());
		dto.setDescription(product.getDescription());
		dto.setPrice(product.getPrice());
		dto.setCategoryId(product.getCategory().getId());
		dto.setQuantity(qty);
		return dto;
	}

	public ProductResponseDTO toggleStatus(Long productId, ProductStatus status) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
		product.setStatus(status);
		Product updated = productRepository.save(product);
		Integer qty = inventoryRepository.findByProductId(productId).map(Inventory::getQuantity).orElse(0);
		return toDTO(updated, qty);
	}

	public ProductResponseDTO updatePrice(Long productId, Double price) {
		if (price == null || price <= 0) {
			throw new InvalidRequestException("Price must be greater than 0");
		}
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
		product.setPrice(price);
		Product updated = productRepository.save(product);
		Integer qty = inventoryRepository.findByProductId(productId).map(Inventory::getQuantity).orElse(0);
		return toDTO(updated, qty);
	}

	public ProductResponseDTO updateInventory(Long productId, Integer quantity) {
		if (quantity == null || quantity < 0) {
			throw new InvalidRequestException("Quantity cannot be negative");
		}
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
		Inventory inventory = inventoryRepository.findByProductId(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));
		inventory.setQuantity(quantity);
		inventoryRepository.save(inventory);
		return toDTO(product, quantity);
	}

	public ProductResponseDTO assignCategory(Long productId, Long categoryId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
		product.setCategory(category);
		Product updated = productRepository.save(product);
		Integer qty = inventoryRepository.findByProductId(productId).map(Inventory::getQuantity).orElse(0);
		return toDTO(updated, qty);
	}

	public List<ProductResponseDTO> getAllProductsForAdmin() {
		return productRepository.findAllByOrderByUpdatedAtDesc().stream().map(
				p -> toDTO(p, inventoryRepository.findByProductId(p.getId()).map(Inventory::getQuantity).orElse(0)))
				.collect(Collectors.toList());
	}

	public List<ProductResponseDTO> getAllProducts() {
		return productRepository.findAllByOrderByUpdatedAtDesc().stream()
				.filter(p -> p.getStatus() == ProductStatus.ENABLED)
				.map(p -> toDTO(p,
						inventoryRepository.findByProductId(p.getId()).map(Inventory::getQuantity).orElse(0)))
				.filter(dto -> dto.getAvailableQuantity() > 0).collect(Collectors.toList());
	}

	public List<ProductResponseDTO> getProductsByCategory(Long categoryId) {
		return productRepository.findByCategoryIdOrderByUpdatedAtDesc(categoryId).stream()
				.filter(p -> p.getStatus() == ProductStatus.ENABLED)
				.map(p -> toDTO(p,
						inventoryRepository.findByProductId(p.getId()).map(Inventory::getQuantity).orElse(0)))
				.filter(dto -> dto.getAvailableQuantity() > 0).collect(Collectors.toList());
	}

	private ProductResponseDTO toDTO(Product product, Integer quantity) {
		return new ProductResponseDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
				product.getStatus(), product.getCategory().getName(), quantity);
	}

}