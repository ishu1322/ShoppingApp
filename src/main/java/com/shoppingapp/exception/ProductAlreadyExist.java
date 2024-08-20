package com.shoppingapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class ProductAlreadyExist extends Exception{
	public ProductAlreadyExist(String message) {
		super(message);
	}
}
