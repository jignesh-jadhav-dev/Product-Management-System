package com.product_order.excception;

public class ResourceInUseException extends RuntimeException {
	public ResourceInUseException(String message) {
		super(message);
	}
}