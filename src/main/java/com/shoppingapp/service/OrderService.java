package com.shoppingapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shoppingapp.model.Order;
import com.shoppingapp.repository.OrderRepository;

import jakarta.validation.Valid;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepo;

	public Order placeOrder(@Valid Order order) {
		return orderRepo.save(order);
	}

	public String getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
	
	
}
