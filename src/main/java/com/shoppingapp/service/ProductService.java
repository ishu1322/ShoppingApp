package com.shoppingapp.service;

import java.util.List;
import java.util.Optional;

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
		
		Optional<Product> productOptional = productRepo.findByNameContainingIgnoreCase(productName)
                .stream()
                .filter(product -> product.getName().equalsIgnoreCase(productName))
                .findFirst();

        if (productOptional.isPresent()) {
            productRepo.delete(productOptional.get());
            log.info("product deleted by admin with name: "+ productName);
        } else {
            throw new ProductsNotFound("Product not found: " + productName);
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

	public void updateQuantity(String productName, int quantity) {
		// TODO Auto-generated method stub
		
	}

	public void updateQuantityandStatus(Product product, int quantity) {
		
		int newQuantity=product.getQuantity()-quantity;
		product.setQuantity(newQuantity);
		product.setStatus(generateStatus(product));
		productRepo.save(product);
		log.info("Quatity updated for product: "+product.getName() + " and Status updated to: " + product.getStatus());
	}
}
