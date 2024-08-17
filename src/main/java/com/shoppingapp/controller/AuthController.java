package com.shoppingapp.controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppingapp.model.ERole;
import com.shoppingapp.model.LoginRequest;

import com.shoppingapp.model.User;

import com.shoppingapp.repository.UserRepository;
import com.shoppingapp.security.JwtTokenUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/shopping/")
public class AuthController {
	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;
    
    

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    
	@PostMapping("/register")
	public String registerUser(@Valid @RequestBody User user) {
        if (userRepository.existsByLoginId(user.getLoginId())) {
            return "Username is already taken!";
        }
        Set<String> roles = new HashSet<>();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        Role role= roleRepository.findByRole();
        roles.add("ROLE_ADMIN");
        System.out.println(roles);
        user.setRoles(roles);
        userRepository.save(user);
        return "User registered successfully!";
    }
	
	
	@GetMapping("/login")
	 public String authenticateUser(@RequestBody LoginRequest user) {
        org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getLoginId(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenUtil.generateToken(user.getLoginId());
        return token;
    }
}
