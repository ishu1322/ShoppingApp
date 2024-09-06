package com.shoppingapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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


@ExtendWith(MockitoExtension.class)
public class AppControllerTest {
	
	 @Mock
	 private ProductService productService;
	 
	 @InjectMocks
	 private AppController appController;
	 
	 @Mock
	 private OrderService orderService;

	  @Mock
	  private KafkaProducer kafkaProducer;
	  
	  @Mock
	  private KafkaConsumer kafkaConsumer;
	 
	 @Test
	  void testViewAllProductsNoProductsFound() {
	        // Arrange
	        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

	        // Act & Assert
	        ProductsNotFound exception = assertThrows(ProductsNotFound.class, () -> {
	        	appController.viewAllProducts();
	        });

	        // Verify the exception message
	        assertThat(exception.getMessage()).isEqualTo("No Products found");
	        verify(productService, times(1)).getAllProducts();
	  }
	    
	 @Test
	 void testViewAllProductsSuccess() throws ProductsNotFound {
	        // Arrange
		 List<String> features = List.of("feature 1");
		 Product product1 = new Product("ID1","Product 1", "Description 1", 100,12,features,"IN STOCK") ;
		 Product product2 = new Product("ID2","Product 2", "Description 2", 300,102,features,"IN STOCK") ;
	     List<Product> productList = List.of(product1, product2);
	     
	        when(productService.getAllProducts()).thenReturn(productList);

	        // Act
	        ResponseEntity<List<Product>> response = appController.viewAllProducts();

	        // Assert
	        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
	        assertThat(response.getBody()).isEqualTo(productList);
	        verify(productService, times(1)).getAllProducts();
	   }
	 
	 @Test
	 void testSearchProductNoResultsFound() {
	        // Arrange
	        String query = "Nonexistent Product";
	        when(productService.searchProductByName(query)).thenReturn(Collections.emptyList());

	        // Act & Assert
	        ProductsNotFound exception = assertThrows(ProductsNotFound.class, () -> {
	            appController.searchProduct(query);
	        });

	        // Verify the exception message
	        assertThat(exception.getMessage()).isEqualTo("No products available with this name: " + query);
	        verify(productService, times(1)).searchProductByName(query);
	    }
	 
	 @Test
	 void testSearchProductSuccess() throws ProductsNotFound {
	        // Arrange
	        String query = "Product 1";
	        List<String> features = List.of("feature 1");
			Product product1 = new Product("ID1","Product 1", "Description 1", 100,12,features,"IN STOCK") ;
			Product product2 = new Product("ID2","Product 2", "Description 2", 300,102,features,"IN STOCK") ;
		    List<Product> productList = List.of(product1, product2);
	        when(productService.searchProductByName(query)).thenReturn(productList);

	        // Act
	        ResponseEntity<List<Product>> response = appController.searchProduct(query);

	        // Assert
	        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
	        assertThat(response.getBody()).isEqualTo(productList);
	        verify(productService, times(1)).searchProductByName(query);
	    }
	 
	    @Test
	    void testAddProductAlreadyExists() {
	        // Arrange
	    	
	        List<String> features = List.of("feature 1");
			Product product1 = new Product("ID1","Product 1", "Description 1", 100,12,features,"IN STOCK") ;
	        String productName = "Existing Product";
	        when(productService.getProductByNameIgnoreCase(productName)).thenReturn(List.of(product1));

	        // Act & Assert
	        ProductAlreadyExist exception = assertThrows(ProductAlreadyExist.class, () -> {
	            appController.addProduct(productName, product1);
	        });

	        // Verify the exception message
	        assertThat(exception.getMessage()).isEqualTo("Product Already Exist by name: " + productName);
	        verify(productService, times(1)).getProductByNameIgnoreCase(productName);
	    }
	    
	    @Test
	    void testAddProductSuccess() throws ProductAlreadyExist {
	        // Arrange
	    	List<String> features = List.of("feature 1");
			Product newProduct = new Product("ID1","New Product", "Description 1", 100,12,features,"IN STOCK") ;
	        String productName = "New Product";
	        when(productService.getProductByNameIgnoreCase(productName)).thenReturn(Collections.emptyList());
	        when(productService.generateStatus(newProduct)).thenReturn("Available");
	        when(productService.addProduct(newProduct)).thenReturn(newProduct);

	        // Act
	        ResponseEntity<Product> response = appController.addProduct(productName, newProduct);

	        // Assert
	        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	        assertThat(response.getBody()).isEqualTo(newProduct);
	        verify(productService, times(1)).getProductByNameIgnoreCase(productName);
	        verify(productService, times(1)).generateStatus(newProduct);
	        verify(productService, times(1)).addProduct(newProduct);
	    }
	    
	    @Test
	    void testUpdateProductStatusProductNotFound() {
	        // Arrange
	    	List<String> features = List.of("feature 1");
			Product updatedProduct = new Product("ID1","Updated Product", "Description 2", 300,102,features,"IN STOCK") ;
	        String productName = "Nonexistent Product";
	        when(productService.getProductByNameIgnoreCase(productName)).thenReturn(Collections.emptyList());

	        // Act & Assert
	        ProductsNotFound exception = assertThrows(ProductsNotFound.class, () -> {
	            appController.updateProductStatus(productName, updatedProduct);
	        });

	        // Verify the exception message
	        assertThat(exception.getMessage()).isEqualTo("No products available with this name: " + productName);
	        verify(productService, times(1)).getProductByNameIgnoreCase(productName);
	    }
	    
	    @Test
	    void testUpdateProductStatusSuccess() throws ProductsNotFound {
	        // Arrange
	    	List<String> features = List.of("feature 1");
			Product existingProduct = new Product("ID1","Existing Product", "Description 1", 100,12,features,"IN STOCK") ;
			Product updatedProduct = new Product("ID1","Updated Product", "Description 2", 300,102,features,"IN STOCK") ;
	        String productName = "Existing Product";
	        when(productService.getProductByNameIgnoreCase(productName)).thenReturn(List.of(existingProduct));
	        when(productService.generateStatus(updatedProduct)).thenReturn("IN STOCK");
	        when(productService.addProduct(existingProduct)).thenReturn(updatedProduct);

	        // Act
	        ResponseEntity<Product> response = appController.updateProductStatus(productName, updatedProduct);

	        // Assert
	        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	        assertThat(response.getBody()).isEqualTo(updatedProduct);
	        verify(productService, times(1)).getProductByNameIgnoreCase(productName);
	        verify(productService, times(1)).generateStatus(updatedProduct);
	        verify(productService, times(1)).addProduct(existingProduct);
	    }
	    
	    @Test
	    void testDeleteProductNotFound() throws ProductsNotFound {
	        // Arrange
	        String productName = "Nonexistent Product";
	        doThrow(new ProductsNotFound("No products available with this name: " + productName))
	                .when(productService).deleteByName(productName);

	        // Act & Assert
	        ProductsNotFound exception = assertThrows(ProductsNotFound.class, () -> {
	            appController.deleteProduct(productName);
	        });

	        // Verify the exception message
	        assertThat(exception.getMessage()).isEqualTo("No products available with this name: " + productName);
	        verify(productService, times(1)).deleteByName(productName);
	    }
	    
	    @Test
	    void testDeleteProductSuccess() throws ProductsNotFound {
	        // Arrange
	        String productName = "Existing Product";
	        doNothing().when(productService).deleteByName(productName);

	        // Act
	        ResponseEntity<ResponseMessage> response = appController.deleteProduct(productName);

	        // Assert
	        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	        assertThat(response.getBody().getMessage()).isEqualTo("Product deleted successfully.");
	        verify(productService, times(1)).deleteByName(productName);
	    }
	    
	    @Test
	    void testOrderProductProductNotFound() {
	        // Arrange

	        String productName = "Nonexistent Product";
	        Order order = new Order();
	        order.setQuantity(5);
	        when(productService.getProductByNameIgnoreCase(productName)).thenReturn(Collections.emptyList());

	        // Act & Assert
	        ProductsNotFound exception = assertThrows(ProductsNotFound.class, () -> {
	            appController.orderProduct(productName, order);
	        });

	        // Verify the exception message
	        assertThat(exception.getMessage()).isEqualTo("No products  available with this name: " + productName);
	        verify(productService, times(1)).getProductByNameIgnoreCase(productName);
	    }

	    @Test
	    void testOrderProductNotEnoughStock() {
	        // Arrange
	    	List<String> features = List.of("feature 1");
			Product product = new Product("ID1","Product A", "Description 1", 100,12,features,"IN STOCK") ;
	        Order order = new Order();
	        order.setQuantity(5);
	        String productName = "Product A";
	        when(productService.getProductByNameIgnoreCase(productName)).thenReturn(List.of(product));
	        order.setQuantity(15); // Request more than available stock

	        // Act & Assert
	        NotEnoughStock exception = assertThrows(NotEnoughStock.class, () -> {
	            appController.orderProduct(productName, order);
	        });

	        // Verify the exception message
	        assertThat(exception.getMessage()).isEqualTo("Not enough quatity present, quatity present: " 
	            + product.getQuantity() + " order recieved for qty: " + order.getQuantity());
	        verify(productService, times(1)).getProductByNameIgnoreCase(productName);
	    }

	    @Test
	    void testOrderProductSuccess() throws ProductsNotFound, NotEnoughStock {
	        // Arrange
	    	List<String> features = List.of("feature 1");
			Product product = new Product("ID1","Product A", "Description 1", 100,12,features,"IN STOCK") ;
	        Order order = new Order();
	        order.setQuantity(5);
	        String productName = "Product A";
	        when(productService.getProductByNameIgnoreCase(productName)).thenReturn(List.of(product));
	        when(orderService.getUser()).thenReturn("User123");
	        when(orderService.placeOrder(order)).thenReturn(order);

	        // Act
	        ResponseEntity<Order> response = appController.orderProduct(productName, order);

	        // Assert
	        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	        assertThat(response.getBody()).isEqualTo(order);
	        verify(productService, times(1)).getProductByNameIgnoreCase(productName);
	        verify(orderService, times(1)).getUser();
	        verify(orderService, times(1)).placeOrder(order);
	        verify(productService, times(1)).updateQuantityandStatus(product, order.getQuantity());
	        (verify(kafkaProducer, times(1))).sendOrderMessage(
	                productName,
	                "Order placed by user: User123" + " at " + order.getOrderDate()+" of product: "
	    					+ order.getProduct()+ ", quantity: "+order.getQuantity()+ ", total price: "+order.getTotalPrice());
	    }
	    
	    @Test
	    public void testGetOrderMessages() throws Exception {
	        // Arrange
	        List<String> mockMessages = List.of("Order1", "Order2", "Order3");
	        when(kafkaConsumer.getMessages()).thenReturn(mockMessages);

	        // Act
	        ResponseEntity<List<String>> response = appController.getOrderMessages();

	        // Assert
	        assertNotNull(response);
	        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	        assertEquals(mockMessages, response.getBody());
	        
	        // Verify that kafkaConsumer.getMessages() was called exactly once
	        verify(kafkaConsumer, times(1)).getMessages();
	    }
}
