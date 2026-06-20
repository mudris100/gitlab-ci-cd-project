package dev.udris.controller.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.udris.dto.UserDto;
import dev.udris.dto.UserRegistrationDto;
import dev.udris.entity.User;
import dev.udris.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

	private UserService userService;

	public UserRestController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("")
	public List<UserDto> findAll() {
		return userService.findAllUsersAsDto();
	}

	@PostMapping("/batch")
	public ResponseEntity<List<User>> createUsers(@RequestBody List<User> users) {
		List<User> createdUsers = userService.createAllUsers(users);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers);
	}

	@GetMapping("/{id}")
	public UserDto findUserDtoById(@PathVariable("id") Integer id) {
		return userService.findUserDtoByIdOrThrow(id);
	}

	@PostMapping("")
	public User createUser(@RequestBody UserRegistrationDto user) {
		return userService.createUser(user);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Integer id) {
		userService.deleteUserById(id);
	}


	@PutMapping("/{id}")
	public User update(@PathVariable Integer id, @RequestBody User user) {
		return userService.updateUser(id, user);
	}

}
