package com.shoppingapp.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.shoppingapp.model.Product;

public interface ProductRepository extends MongoRepository<Product, ObjectId>{

	List<Product> findByNameIgnoreCase(String productName);
	List<Product> findByNameContainingIgnoreCase(String productName);
	

}
