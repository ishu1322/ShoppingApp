package com.shoppingapp.security;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.shoppingapp.model.User;

public class CustomUserDetails implements UserDetails {

	 private final User user;

	    public CustomUserDetails(User user) {
	        this.user = user;
	    }
	    
	    

	    @Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
//	    	 return user.getRoles().stream()
//	    	            .filter(role -> role != null && role.getRole() != null) // Add null checks
//	    	            .map(role -> role.getRole())
//	    	            .collect(Collectors.toSet());
	    	return user.getRoles().stream()
	                .map(role -> new SimpleGrantedAuthority(role.name())) // Prefix with "ROLE_"
	                .collect(Collectors.toList());
	    }

	    @Override
	    public String getPassword() {
	        return user.getPassword();
	    }

	    @Override
	    public String getUsername() {
	        return user.getLoginId();
	    }

	    @Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }

	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isEnabled() {
	        return true;
	    }

}
