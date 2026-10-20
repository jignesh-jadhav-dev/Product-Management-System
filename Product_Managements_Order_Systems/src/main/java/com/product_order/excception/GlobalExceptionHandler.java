package com.product_order.excception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
		return new ResponseEntity<>(
				new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage()),
				HttpStatus.CONFLICT);
	}

	@ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
		return new ResponseEntity<>(
				new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex) {
		return new ResponseEntity<>(
				new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceInUseException.class)
	public ResponseEntity<ErrorResponse> handleCategoryInUse(ResourceInUseException ex) {
		return new ResponseEntity<>(
				new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage()),
				HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
		return new ResponseEntity<>(
				new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage()),
				HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
		return new ResponseEntity<>(
				new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}