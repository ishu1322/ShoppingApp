package com.shoppingapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ProductsNotFound extends Exception{
	public ProductsNotFound(String message) {
		super(message);
	}
}
