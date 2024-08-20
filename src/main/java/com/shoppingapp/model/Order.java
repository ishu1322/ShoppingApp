package com.shoppingapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
public class Order {
	
	@Id
    private ObjectId id;

    @NotBlank
    private String product;

    
    private String customer;  

    @NotNull
    private int quantity;

    @NotNull
    private double totalPrice;
    
    private LocalDateTime orderDate=LocalDateTime.now();
}
