package com.shoppingapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.shoppingapp.exception.ProductAlreadyExist;
import com.shoppingapp.exception.ProductsNotFound;
import com.shoppingapp.model.Product;
import com.shoppingapp.service.ProductService;


@ExtendWith(MockitoExtension.class)
public class AppControllerTest {
	
	 @Mock
	 private ProductService productService;
	 
	 @InjectMocks
	 private AppController appController;
	 
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
}
