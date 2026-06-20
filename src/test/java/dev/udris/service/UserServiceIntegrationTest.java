package dev.udris.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import dev.udris.dto.UserRegistrationDto;
import dev.udris.entity.Role;
import dev.udris.entity.User;
import dev.udris.exception.UsernameAlreadyTakenException;
import dev.udris.service.UserService;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test") // Load application-test.properties with H2 settings
@Transactional // Roll back DB changes after each test
class UserServiceIntegrationTest {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void createUser_shouldSetDefaultsAndEncodePassword() {
		UserRegistrationDto user = new UserRegistrationDto();
		user.setUsername("martins");
		user.setPassword("plainPassword");
		User saved = userService.createUser(user);
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getRole()).isEqualTo(Role.USER);
		assertThat(passwordEncoder.matches("plainPassword", saved.getPassword())).isTrue();
	}

	@Test
	void createUser_shouldThrowIfUsernameAlreadyExists() {
		UserRegistrationDto user1 = new UserRegistrationDto();
		user1.setUsername("martins");
		user1.setPassword("plainPassword");
		userService.createUser(user1);

		UserRegistrationDto user2 = new UserRegistrationDto();
		user2.setUsername("martins");
		user2.setPassword("another");

		assertThatThrownBy(() -> userService.createUser(user2)).isInstanceOf(UsernameAlreadyTakenException.class)
				.hasMessageContaining("Username already taken");
	}

	@Test
	void createUser_shouldKeepProvidedEmailAndRole() {
		UserRegistrationDto user = new UserRegistrationDto();
		user.setUsername("admin");
		user.setPassword("1234");
		user.setEmail("admin@example.com");
		User saved = userService.createUser(user);
		saved.setRole(Role.ADMIN);

		assertThat(saved.getEmail()).isEqualTo("admin@example.com");
		assertThat(saved.getRole()).isEqualTo(Role.ADMIN);
	}
}