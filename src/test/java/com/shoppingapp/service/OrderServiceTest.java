package com.shoppingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.shoppingapp.model.Order;
import com.shoppingapp.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
	
	 	@Mock
	    private OrderRepository orderRepo;

	    @InjectMocks
	    private OrderService orderService;
	    
	    @Test
	    public void testPlaceOrder() {
	        Order order = new Order();
	        
	        order.setProduct("Test Product");

	        when(orderRepo.save(order)).thenReturn(order);

	        Order savedOrder = orderService.placeOrder(order);
	        
	        assertEquals("Test Product", savedOrder.getProduct());
	    }

	    @Test
	    public void testGetUser() {
	    	
	    	SecurityContextHolder.getContext().setAuthentication(
	                new TestingAuthenticationToken("testUser", "password", "ROLE_USER")
	            );
	        String userName = orderService.getUser();
	        assertEquals("testUser", userName);
	    }

}
