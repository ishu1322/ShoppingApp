package com.shoppingapp.model;


import java.util.Set;


import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
//	@Id
//	private ObjectId _id;
	
	@NotBlank
	@Id
	@Size(max = 20)
	private String loginId;
	
	@NotBlank
	@Size(max = 20)
	private String firstName;
	
	@NotBlank
	@Size(max = 20)
	private String lastName;
	
	@NotBlank
	@Email
	@Size(max = 20)
	private String email;
	
	@NotNull
	private Long contactNumber;
	
	@NotBlank
	@Size(max = 50)
	private String password;
	

	@NotNull(message = "Roles cannot be null")
    @NotEmpty(message = "Roles cannot be empty")
	private Set<Role> roles;
	
	public User(String loginId,String password,Set<Role> roles) {
		this.loginId=loginId;
		this.password=password;
		this.roles=roles;
	}
	
}
