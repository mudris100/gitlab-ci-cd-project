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
import org.springframework.web.bind.annotation.RestController;

import dev.udris.dto.CommentDto;
import dev.udris.dto.post.PostDto;
import dev.udris.dto.post.PostSummaryDto;
import dev.udris.dto.post.UpdatePostDto;
import dev.udris.entity.Post;
import dev.udris.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

	private PostService postService;

	public PostRestController(PostService postService) {
		this.postService = postService;
	}

	@GetMapping
	public List<PostSummaryDto> findAll() {
		return postService.findAllPublished();
	}

	@GetMapping("/{id}")
	public PostDto findById(@PathVariable("id") Long id) {
		return postService.findPublishedById(id);
	}

	@GetMapping("/{id}/comments")
	public List<CommentDto> findCommentsByPost(@PathVariable("id") Long id, Post post) {
		return postService.findCommentsByPost(post);
	}

	@PostMapping
	public Post createPost(@RequestBody Post post) {
		return postService.createPost(post);
	}

	@PutMapping("/{id}")
	public UpdatePostDto update(@PathVariable Long id, @RequestBody Post post) {
		return postService.update(id, post);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		postService.deletePostById(id);
		return ResponseEntity.noContent().build();
//		To get Recommended Status Code: 204 No Content instead of 200
	}
}
