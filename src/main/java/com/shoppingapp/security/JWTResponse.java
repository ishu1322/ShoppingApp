package com.shoppingapp.security;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JWTResponse {
	private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;
    
    
    public JWTResponse(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }
    
    

}
