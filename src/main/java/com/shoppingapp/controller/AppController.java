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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shoppingapp.exception.NotEnoughStock;
import com.shoppingapp.exception.ProductAlreadyExist;
import com.shoppingapp.exception.ProductsNotFound;
import com.shoppingapp.kafka.KafkaConsumer;
import com.shoppingapp.kafka.KafkaProducer;
import com.shoppingapp.model.Order;
import com.shoppingapp.model.Product;
import com.shoppingapp.model.ResponseMessage;
import com.shoppingapp.service.OrderService;
import com.shoppingapp.service.ProductService;

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
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
    private KafkaProducer kafkaProducer;
	
	@Autowired
	private KafkaConsumer kafkaConsumer;
	

	
	// http://localhost:8081/api/v1.0/shopping/all
	@GetMapping("all")
	@SecurityRequirement(name = "Bearer Authentication")
	@PreAuthorize("hasRole('USER')")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "get all products")
	public ResponseEntity<List<Product>> viewAllProducts() throws ProductsNotFound {
		
		
		List<Product> productList = productService.getAllProducts();
		
		if(productList.isEmpty()) {
			log.warn("No products available at the moment.");
			throw new ProductsNotFound("No Products found");
		}
		log.info("All products accessed");
		return new ResponseEntity<>(productList ,HttpStatus.FOUND);
	}
	
//	http://localhost:8081/api/v1.0/shopping/products/search?query=
	@PreAuthorize("hasRole('USER')")
	@GetMapping("products/search")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "search products by name")
	public ResponseEntity<List<Product>> searchProduct(@RequestParam("query") String name) 
			throws ProductsNotFound {
		List<Product> productList=productService.searchProductByName(name);
		
		if(productList.isEmpty()) {
			log.warn("No search result found with query: "+name);
			throw new ProductsNotFound("No products available with this name: "+name);
		}
		
		log.info("Search result return for query: "+name);
		return new ResponseEntity<>(productList ,HttpStatus.FOUND);
	}
	
//	http://localhost:8081/api/v1.0/shopping/{productName}/add
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "add new product (admin)")
	@PostMapping("{productName}/add")
	public ResponseEntity<Product> addProduct(@PathVariable String productName,@Valid @RequestBody Product product) 
			throws ProductAlreadyExist {
		
		List<Product> products = productService.getProductByNameIgnoreCase(productName);
		
		if(!products.isEmpty()) {
			log.warn("Cannot add existing Product :"+productName);
			throw new ProductAlreadyExist("Product Already Exist by name: "+productName);
		}
		
		product.setStatus(productService.generateStatus(product));
		Product savedProduct= productService.addProduct(product);
		log.info("new product added: "+productName);
		return ResponseEntity.ok(savedProduct);
	}
	
	
//	http://localhost:8081/api/v1.0/shopping/iphone/update
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{productName}/update")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "update product (admin)")
	public ResponseEntity<Product> updateProductStatus(@PathVariable String productName,@Valid @RequestBody Product product) 
			throws ProductsNotFound {
		
		List<Product> products = productService.getProductByNameIgnoreCase(productName);
		
		if(products.isEmpty()) {
			log.warn("No product found with name: "+productName);
			throw new ProductsNotFound("No products available with this name: "+productName);
		}
		
		Product existingProduct = products.get(0);
		
		existingProduct.setDescription(product.getDescription());
		existingProduct.setFeatures(product.getFeatures());
		existingProduct.setPrice(product.getPrice());
		existingProduct.setQuantity(product.getQuantity());
		existingProduct.setStatus(productService.generateStatus(product));
		
		Product savedProduct=productService.addProduct(existingProduct);
		log.info("Product updated by admin: "+productName);
		return ResponseEntity.ok(savedProduct);
	}
	
//	http://localhost:8081/api/v1.0/shopping/{productName}/delete
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("{productName}/delete")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "delete products (admin)")
	public ResponseEntity<ResponseMessage> deleteProduct(@PathVariable String productName) throws ProductsNotFound  {
				
		productService.deleteByName(productName);
		
		return ResponseEntity.ok(new ResponseMessage("Product deleted successfully."));
	}
	
	
	
//	http://localhost:8081/api/v1.0/shopping/{productName}/order
		@PreAuthorize("hasRole('USER')")
		@SecurityRequirement(name = "Bearer Authentication")
		@Operation(summary = "order product")
		@PostMapping("{productName}/order")
		public ResponseEntity<Order> orderProduct(@PathVariable String productName,@Valid @RequestBody Order order) 
				throws ProductsNotFound, NotEnoughStock{
			
			List<Product> products = productService.getProductByNameIgnoreCase(productName);
			
			if(products.isEmpty()) {
				log.warn("No product found with name: "+productName);
				throw new ProductsNotFound("No products  available with this name: "+productName);
			}
			
			Product product = products.get(0);
			if(product.getQuantity()<order.getQuantity()) {
				log.warn("Not enough quatity present for product: "+productName);
				throw new NotEnoughStock("Not enough quatity present, quatity present: "+product.getQuantity() +" order recieved for qty: "+ order.getQuantity());
			}
			
			String user=orderService.getUser();
			order.setCustomer(user);
			order.setTotalPrice(product.getPrice()*order.getQuantity());
			Order placedOrder = orderService.placeOrder(order);
			productService.updateQuantityandStatus(product,placedOrder.getQuantity());
			
			log.info("order placed by user: "+user + " at " + placedOrder.getOrderDate()+" of product: "
			+ placedOrder.getProduct()+ " quantity: "+placedOrder.getQuantity()+ " total price: "+placedOrder.getTotalPrice());
			
			kafkaProducer.sendOrderMessage(productName,"Order placed by user: "+user + " at " + placedOrder.getOrderDate()+" of product: "
					+ placedOrder.getProduct()+ ", quantity: "+placedOrder.getQuantity()+ ", total price: "+placedOrder.getTotalPrice());
			
			return ResponseEntity.ok(placedOrder);
			
		}
		
		@PreAuthorize("hasRole('ADMIN')")
		@SecurityRequirement(name = "Bearer Authentication")
		@Operation(summary = "getch kafka order messages (admin)")
		@GetMapping("getOrders")
		public ResponseEntity<List<String>> getOrderMessages(){ 
			log.info("kafka order messages accessed by admin");
			return ResponseEntity.ok(kafkaConsumer.getMessages());
		}
	
	
}
