package dev.udris.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.udris.dto.UserRegistrationDto;
import dev.udris.exception.UsernameAlreadyTakenException;
import dev.udris.service.UserService;
import jakarta.validation.Valid;

@Controller
public class LoginController {

	private final UserService service;

	public LoginController(UserService service) {
		this.service = service;
	}

	@GetMapping("/login")
	public String newUser(@ModelAttribute("dto") UserRegistrationDto dto) {
		return "auth/login";
	}

	@PostMapping("/register")
	public String create(@ModelAttribute("dto") @Valid UserRegistrationDto dto, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "auth/login";
		}
		try {
			service.createUser(dto);
			redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
		} catch (UsernameAlreadyTakenException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/login";
	}
}
