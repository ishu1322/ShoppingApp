package com.shoppingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shoppingapp.exception.ProductsNotFound;
import com.shoppingapp.model.Product;
import com.shoppingapp.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
	
	@Mock
    private ProductRepository productRepo;

    @InjectMocks
    private ProductService productService;
    
    
    @Test
    public void testGetAllProducts() {
    	List<String> features = List.of("feature 1");
		Product product1 = new Product("ID1","Product 1", "Description 1", 100,12,features,"IN STOCK") ;
		Product product2 = new Product("ID2","Product 2", "Description 2", 200,12,features,"IN STOCK") ;
        List<Product> productList = Arrays.asList(product1, product2);

        when(productRepo.findAll()).thenReturn(productList);

        List<Product> result = productService.getAllProducts();
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getName());
        assertEquals("Product 2", result.get(1).getName());
    }
    
    @Test
    public void testSearchProductByName() {
        String productName = "Product";
        List<String> features = List.of("feature 1");
		Product product1 = new Product("ID1","Product 1", "Description 1", 100,12,features,"IN STOCK") ;
		Product product2 = new Product("ID2","Product 2", "Description 2", 200,12,features,"IN STOCK") ;
        List<Product> productList = Arrays.asList(product1, product2);

        when(productRepo.findByNameContainingIgnoreCase(productName)).thenReturn(productList);

        List<Product> result = productService.searchProductByName(productName);
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getName());
        assertEquals("Product 2", result.get(1).getName());
    }
	
    @Test
    public void testAddProduct() {
    	 List<String> features = List.of("feature 1");
 		Product product = new Product("ID1","Product 1", "Description 1", 10,12,features,"IN STOCK") ;

        when(productRepo.save(product)).thenReturn(product);

        Product result = productService.addProduct(product);
        assertEquals("Product 1", result.getName());
        assertEquals(10.0, result.getPrice());
    }
    
    @Test
    public void testGetProductByNameIgnoreCase() {
        String productName = "product";
        List<String> features = List.of("feature 1");
 		Product product1 = new Product("ID1","Product", "Description 1", 10,12,features,"IN STOCK") ;
        List<Product> productList = Arrays.asList(product1);

        when(productRepo.findByNameIgnoreCase(productName)).thenReturn(productList);

        List<Product> result = productService.getProductByNameIgnoreCase(productName);
        assertEquals(1, result.size());
        assertEquals("Product", result.get(0).getName());
    }
    @Test
    public void testDeleteByName_ProductExists() throws ProductsNotFound {
        String productName = "Product";
        List<String> features = List.of("feature 1");
 		Product product1 = new Product("ID1","Product", "Description 1", 10,12,features,"IN STOCK") ;
        List<Product> productList = Arrays.asList(product1);

        when(productRepo.findByNameContainingIgnoreCase(productName)).thenReturn(productList);

        productService.deleteByName(productName);

        verify(productRepo, times(1)).deleteByName(product1.getName());
    }
    
    @Test
    public void testDeleteByName_ProductNotFound() {
        String productName = "NonExistingProduct";

        when(productRepo.findByNameContainingIgnoreCase(productName)).thenReturn(Collections.emptyList());

        ProductsNotFound exception = assertThrows(ProductsNotFound.class, () -> {
            productService.deleteByName(productName);
        });

        assertEquals("Product not found: " + productName, exception.getMessage());
        verify(productRepo, never()).deleteByName(anyString());
    }
    
    @Test
    public void testGenerateStatus_OutOfStock() {
        Product product = new Product();
        product.setQuantity(0);

        String status = productService.generateStatus(product);
        assertEquals("OUT OF STOCK", status);
    }

    @Test
    public void testGenerateStatus_HurryUpToPurchase() {
        Product product = new Product();
        product.setQuantity(5);

        String status = productService.generateStatus(product);
        assertEquals("HURRY UP TO PURCHASE", status);
    }

    @Test
    public void testGenerateStatus_InStock() {
        Product product = new Product();
        product.setQuantity(20);

        String status = productService.generateStatus(product);
        assertEquals("IN STOCK", status);
    }
    
    @Test
    public void testUpdateQuantityandStatus() {
        Product product = new Product();
        product.setName("Test Product");
        product.setQuantity(15);

        int quantityToDeduct = 5;
        productService.updateQuantityandStatus(product, quantityToDeduct);

        assertEquals(10, product.getQuantity());
        assertEquals("HURRY UP TO PURCHASE", product.getStatus());
        verify(productRepo, times(1)).save(product);
    }

    @Test
    public void testUpdateQuantityandStatus_OutOfStock() {
        Product product = new Product();
        product.setName("Test Product");
        product.setQuantity(3);

        int quantityToDeduct = 3;
        productService.updateQuantityandStatus(product, quantityToDeduct);

        assertEquals(0, product.getQuantity());
        assertEquals("OUT OF STOCK", product.getStatus());
        verify(productRepo, times(1)).save(product);
    }
}
