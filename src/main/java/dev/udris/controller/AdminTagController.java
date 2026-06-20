package dev.udris.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.udris.dto.TagDto;
import dev.udris.entity.Tag;
import dev.udris.service.TagService;

@Controller
@RequestMapping("/admin/tags")
public class AdminTagController {

	private final TagService tagService;

	public AdminTagController(TagService tagService) {
		this.tagService = tagService;
	}

	@GetMapping
	public String listTags(Model model) {
		List<TagDto> tags = tagService.findAll();
		model.addAttribute("tags", tags);
		return "tags/list";
	}

	@GetMapping("/new")
	public String showCreateForm(Model model) {
		model.addAttribute("tag", new Tag());
		return "tags/form";
	}

	@PostMapping
	public String createTag(@ModelAttribute Tag tag) {
		tagService.createTag(tag.getName());
		return "redirect:/admin/tags";
	}

	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, Model model) {
		Tag tag = tagService.findByIdOrThrow(id);
		model.addAttribute("tag", tag);
		return "tags/edit";
	}

	@PostMapping("/{id}/edit")
	public String updateTag(@PathVariable Long id, @ModelAttribute Tag tag) {
		tagService.updateTag(id, tag.getName());
		return "redirect:/admin/tags";
	}

	@PostMapping("/{id}/delete")
	public String deleteTag(@PathVariable Long id) {
		tagService.deleteTag(id);
		return "redirect:/admin/tags";
	}
} 