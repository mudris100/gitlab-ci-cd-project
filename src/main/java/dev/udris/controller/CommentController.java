package dev.udris.controller;

import dev.udris.dto.CommentDto;
import dev.udris.entity.Comment;
import dev.udris.oauth2.CustomUserPrincipal;
import dev.udris.service.CommentService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
public class CommentController {
	private final CommentService commentService;

	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	@GetMapping("/{id}/delete")
	public String deleteComment(@PathVariable Long id, @AuthenticationPrincipal CustomUserPrincipal principal) {
		CommentDto commentDto = commentService.findCommentDtoByIdOrThrow(id);
		if (!commentDto.author().equals(principal.getName())) {
			throw new AccessDeniedException("You are not allowed to delete this comment.");
		}
		commentService.deleteComment(id);
		return "redirect:/posts/" + commentDto.postId();
	}

	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, Model model) {
		CommentDto commentDto = commentService.findCommentDtoByIdOrThrow(id);
		model.addAttribute("commentDto", commentDto);
		return "comments/edit";
	}
	
	@PostMapping("/{id}")
	public String update(@PathVariable Long id, @ModelAttribute("commentDto") CommentDto commentDto, @AuthenticationPrincipal CustomUserPrincipal principal) {
		Comment comment = commentService.update(id, commentDto, principal.getName());
		return "redirect:/posts/"+ comment.getPost().getId();
	}
}