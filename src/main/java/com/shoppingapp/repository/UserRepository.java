package com.shoppingapp.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shoppingapp.model.User;

public interface UserRepository extends MongoRepository<User,String> {
	Optional<User> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
}
