package com.shoppingapp.controller;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.shoppingapp.model.LoginRequest;
import com.shoppingapp.model.Role;
import com.shoppingapp.model.User;

import com.shoppingapp.repository.UserRepository;
import com.shoppingapp.security.CustomUserDetails;
import com.shoppingapp.security.JWTResponse;
import com.shoppingapp.security.JwtTokenUtil;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	@Operation(summary = "register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        if (userRepository.existsByLoginId(user.getLoginId())) {
            return ResponseEntity.badRequest().body("Error: LoginId Already Exist!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Error: User already exist with this Email!");
        }
        Set<Role> roles = new HashSet<>();
        for(Role role: user.getRoles()) {
        	if(role.equals(Role.ROLE_USER)){
        		roles.add(Role.ROLE_USER);
        	}else {
        		roles.add(Role.ROLE_ADMIN);
        	}     	
        	
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        System.out.println(roles);
        user.setRoles(roles);
        userRepository.save(user);
        log.info("New user registeres with username: "+ user.getLoginId());
        return ResponseEntity.ok("User registered successfully!");
    }
	
	
	@GetMapping("/login")
	@Operation(summary = "login")
	 public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest user) {
        
		Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getLoginId(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String token = jwtTokenUtil.generateToken(authentication);
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        log.info("User logged in with username: "+ userDetails.getUsername());
        return ResponseEntity.ok(new JWTResponse(token, 
        		userDetails.getUsername(), 
        		roles));
    }
}
