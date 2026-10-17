package com.product_order.excception;

public class DuplicateResourceException extends RuntimeException {
	public DuplicateResourceException(String message) {
		super(message);
	}
}