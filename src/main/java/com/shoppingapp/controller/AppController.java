package com.shoppingapp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/shopping/")
public class AppController {
	
	// http://localhost:8081/api/v1.0/shopping/all
	@GetMapping("all")
	@PreAuthorize("hasRole('ADMIN')")
	public String viewAllProducts() {
		
		System.out.println("view all");
		return "All products";
	}
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("products/search/{productName}")
	public String searchProduct(@PathVariable String productName) {
		
		return "Product search "+productName;
	}
	
	@PostMapping("{productName}/add")
	public String addProduct() {
		return "Product Added";
	}
	
	@PutMapping("/{productName}/update/{productStatus}")
	public String updateProductStatus() {
		return "Status updated";
	}
	
	@DeleteMapping("{productName}/delete")
	public String deleteProduct() {
		return "deleted";
	}
}
