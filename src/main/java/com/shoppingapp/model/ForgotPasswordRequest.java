package com.shoppingapp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {
	@NotBlank
	@Size(max = 20)
	private String loginId;
	@NotBlank
	@Size(max = 20)
	private String Email;
	@NotBlank
	@Size(max = 20)
	private String firstName;
	@NotBlank
	@Size(max = 20)
	private String lastName;
	@NotNull
	private Long contactNumber;
	@NotBlank
	@Size(max = 120)
	private String newPassword;
	
}
