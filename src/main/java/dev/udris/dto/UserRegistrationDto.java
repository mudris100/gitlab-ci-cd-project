package dev.udris.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {
	@NotEmpty(message = "Username should not be empty")
	@Size(min = 3, max = 10, message = "Login length 3-10 characters")
	private String username;
	@NotEmpty(message = "Enter Password")
	@Size(min = 2, max = 100, message = "Password at least 2 characters")
	private String password;
	@Email(message = "Email should be valid")
    @Size(max = 20, message = "Do not exaggerate")
	private String email;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
