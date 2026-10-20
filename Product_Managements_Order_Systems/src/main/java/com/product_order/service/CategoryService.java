package com.product_order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.product_order.dto.CategoryDTO;
import com.product_order.entity.Category;
import com.product_order.excception.DuplicateResourceException;
import com.product_order.excception.ResourceInUseException;
import com.product_order.excception.ResourceNotFoundException;
import com.product_order.repository.CategoryRepository;
import com.product_order.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;

	public CategoryDTO createCategory(CategoryDTO dto) {
		if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
			throw new DuplicateResourceException("Category already exists with name: " + dto.getName());
		}
		Category category = new Category();
		category.setName(dto.getName());
		Category saved = categoryRepository.save(category);
		return new CategoryDTO(saved.getId(), saved.getName());
	}

	public List<CategoryDTO> getAllCategories() {
		return categoryRepository.findAll().stream().map(c -> new CategoryDTO(c.getId(), c.getName()))
				.collect(Collectors.toList());
	}

	public void deleteCategory(Long id) {
		if (!categoryRepository.existsById(id)) {
			throw new ResourceNotFoundException("Category not found with id: " + id);
		}
		if (productRepository.existsByCategoryId(id)) {
			throw new ResourceInUseException(
					"Cannot delete category — it has products assigned to it. Remove or reassign those products first.");
		}
		categoryRepository.deleteById(id);
	}
}