package com.shoppingapp.security;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shoppingapp.model.User;
import com.shoppingapp.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	@Autowired
    private UserRepository userRepository;
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return new CustomUserDetails(user);
    }
}
