package com.product_order.entity;

import java.time.LocalDateTime;

import com.product_order.enums.ProductStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Product name is required")
	private String name;

	private String description;

	@Positive(message = "Price must be positive")
	private Double price;

	@Enumerated(EnumType.STRING)
	private ProductStatus status;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
	private Inventory inventory;

	private LocalDateTime updatedAt;

	@PrePersist
	@PreUpdate
	public void touchUpdatedAt() {
		this.updatedAt = LocalDateTime.now();
	}
}