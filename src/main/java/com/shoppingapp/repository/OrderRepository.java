package com.shoppingapp.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.shoppingapp.model.Order;

public interface OrderRepository extends MongoRepository<Order, String>{

}
