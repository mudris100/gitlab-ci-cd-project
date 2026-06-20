package dev.udris.controller.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.udris.dto.CommentDto;
import dev.udris.entity.Comment;
import dev.udris.service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

	private final CommentService service;

	public CommentRestController(CommentService service) {
		this.service = service;
	}

	@GetMapping("")
	public List<CommentDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public CommentDto findById(@PathVariable Long id) {
		return service.findCommentDtoByIdOrThrow(id);
	}

	@PostMapping("")
	public Comment createComment(@RequestBody Comment comment) {
		return service.createComment(comment);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.deleteComment(id);
		return ResponseEntity.noContent().build();
	}
}
