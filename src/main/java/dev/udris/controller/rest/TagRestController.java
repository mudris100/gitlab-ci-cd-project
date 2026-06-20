package dev.udris.controller.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.udris.dto.TagDto;
import dev.udris.entity.Tag;
import dev.udris.service.TagService;

@RestController
@RequestMapping("/api/tags")
public class TagRestController {

	private final TagService tagService;

	public TagRestController(TagService tagService) {
		this.tagService = tagService;
	}

	@GetMapping
	public List<TagDto> findAll() {
		return tagService.findAll();
	}

	@GetMapping("/search")
	public List<TagDto> searchTags(@RequestParam String name) {
		return tagService.findByNameContaining(name);
	}

	@GetMapping("/popular")
	public List<TagDto> findMostUsedTags() {
		return tagService.findMostUsedTags();
	}

	@GetMapping("/{id}")
	public TagDto findById(@PathVariable Long id) {
		return tagService.findTagDtoById(id);
	}

	@PostMapping
	public Tag createTag(@RequestBody Tag tag) {
		return tagService.createTag(tag.getName());
	}

	@PutMapping("/{id}")
	public TagDto updateTag(@PathVariable Long id, @RequestBody Tag tag) {
		return tagService.updateTag(id, tag.getName());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
		tagService.deleteTag(id);
		return ResponseEntity.noContent().build();
	}
} 