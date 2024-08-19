package com.shoppingapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppingapp.ProductService;
import com.shoppingapp.exception.ProductsNotFound;
import com.shoppingapp.model.Product;
import com.shoppingapp.model.ResponseMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/shopping/")
public class AppController {
	
	@Autowired
	private ProductService productService;
	
	// http://localhost:8081/api/v1.0/shopping/all
	@GetMapping("all")
	@SecurityRequirement(name = "Bearer Authentication")
	@PreAuthorize("hasRole('USER')")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "get all products")
	public ResponseEntity<List<Product>> viewAllProducts() throws ProductsNotFound {
		
		
		List<Product> productList = productService.getAllProducts();
		
		if(productList.isEmpty()) {
			log.info("No products available at the moment.");
			throw new ProductsNotFound("No Products found");
		}
		log.info("All products accessed");
		return new ResponseEntity<List<Product>>(productList ,HttpStatus.FOUND);
	}
	
//	http://localhost:8081/api/v1.0/shopping/products/search/ph
	@PreAuthorize("hasRole('USER')")
	@GetMapping("products/search/{productName}")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "search products by name")
	public ResponseEntity<List<Product>> searchProduct(@PathVariable String productName) throws ProductsNotFound {
		List<Product> productList=productService.searchProductByName(productName);
		
		if(productList.isEmpty()) {
			log.info("No search result found with query: "+productName);
			throw new ProductsNotFound("No products  available with this name");
		}
		
		
		return new ResponseEntity<List<Product>>(productList ,HttpStatus.FOUND);
	}
	
//	http://localhost:8081/api/v1.0/shopping/{productName}/add
	@PostMapping("{productName}/add")
	public ResponseEntity<Product> addProduct(@PathVariable String productName,@Valid @RequestBody Product product) {
		Product savedProduct= productService.addProduct(product);
		return ResponseEntity.ok(savedProduct);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{productName}/update/{productStatus}")
	public String updateProductStatus() {
		return "Status updated";
	}
	
//	http://localhost:8081/api/v1.0/shopping/{productName}/delete
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("{productName}/delete")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "delete products (admin)")
	public ResponseEntity<?> deleteProduct(@PathVariable String productName) throws ProductsNotFound  {
				
		productService.deleteByName(productName);
		
		return ResponseEntity.ok(new ResponseMessage("Product deleted successfully."));
	}
}
