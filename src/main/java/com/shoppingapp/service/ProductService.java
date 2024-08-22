package com.shoppingapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shoppingapp.exception.ProductsNotFound;
import com.shoppingapp.model.Product;
import com.shoppingapp.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepo;
	
	public List<Product> getAllProducts(){
		return productRepo.findAll();
	}

	public List<Product> searchProductByName(String productName) {
		
		return productRepo.findByNameContainingIgnoreCase(productName);
	}

	public Product addProduct(Product product) {
		return productRepo.save(product);
		
	}

	public List<Product> getProductByNameIgnoreCase(String productName) {
		
		return productRepo.findByNameIgnoreCase(productName);
	}

	public void deleteByName(String productName) throws ProductsNotFound {
		
		List<Product> products = productRepo.findByNameContainingIgnoreCase(productName);
               

        if (products.isEmpty()) {
        	
        	throw new ProductsNotFound("Product not found: " + productName);
            
        } else {
        	productRepo.deleteByName(products.get(0).getName());
        	
            log.info("product "+ "'"+productName + "'" +" deleted by admin");
        }
		
	}
	
	public String generateStatus(Product product) {
		
		int quantity = product.getQuantity();
		if(quantity<=0) {
			return "OUT OF STOCK";
		}
		else if(quantity>0 && quantity<=10) {
			return "HURRY UP TO PURCHASE";
			
		}else {
			return "IN STOCK";
		}
		
	}



	public void updateQuantityandStatus(Product product, int quantity) {
		
		int newQuantity=product.getQuantity()-quantity;
		product.setQuantity(newQuantity);
		product.setStatus(generateStatus(product));
		productRepo.save(product);
		log.info("Quatity updated for product: "+product.getName() + " and Status updated to: " + product.getStatus());
	}
}
