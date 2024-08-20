package com.shoppingapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class NotEnoughStock extends Exception{
	public NotEnoughStock(String message) {
		super(message);
	}
}
