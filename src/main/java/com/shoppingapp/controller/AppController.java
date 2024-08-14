package com.shoppingapp.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/shopping/")
public class AppController {
	
	// http://localhost:8081/api/v1.0/shopping/all
	@GetMapping("all")
	public String viewAllProducts() {
		
		return "All products";
	}
	
	
	@GetMapping("products/search/{productName}")
	public String searchProduct() {
		
		return "Product search";
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
