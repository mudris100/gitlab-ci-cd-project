package dev.udris.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.udris.entity.User;
import dev.udris.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
	private final UserService service;

	public AdminUserController(UserService service) {
		this.service = service;
	}

	@GetMapping()
	public String listUsers(Model model) {
		model.addAttribute("users", service.findAllUsersAsDto());
		return "admin/list";
	}
	
	@GetMapping("/{id}")
	public String viewUser(@PathVariable Integer id, Model model) {
		model.addAttribute("author", service.findUserDtoByIdOrThrow(id));
		return "admin/view";
	}
	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Integer id, Model model) {
		model.addAttribute("user", service.findUserByIdOrThrow(id));
		return "admin/edit";
	}
	
	@PostMapping("/{id}/delete")
	public String deleteAuthor(@PathVariable Integer id) {
		service.deleteUserById(id);
		return "redirect:/admin/users";
	}
	
	@PostMapping("/{id}/edit")
	public String editAuthor(@PathVariable Integer id, @ModelAttribute("user") User user) {
		service.updateUser(id, user);
		return "redirect:/admin/users/" + id;
	}
}
