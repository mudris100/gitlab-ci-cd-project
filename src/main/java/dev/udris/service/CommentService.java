package dev.udris.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import dev.udris.dto.CommentDto;
import dev.udris.entity.Comment;
import dev.udris.entity.Post;
import dev.udris.exception.NotFoundException;
import dev.udris.repository.CommentRepository;

@Service
public class CommentService {
	private final CommentRepository repository;

	public CommentService(CommentRepository repository) {
		this.repository = repository;
	}

	public Comment createComment(Comment comment) {
		if (comment.getAuthor() != null) {
			comment.setName(comment.getAuthor().getUsername());
		}
		return repository.save(comment);
	}

	public Comment findByIdOrThrow(Long id) {
		return repository.findById(id).orElseThrow(() -> new NotFoundException("Comment", "id", id));
	}

	public List<CommentDto> findAll() {
		return repository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	public List<CommentDto> findByPost(Post post) {
		return repository.findByPost(post).stream().map(this::mapToDto).collect(Collectors.toList());
	}

	public CommentDto findCommentDtoByIdOrThrow(Long id) {
		Comment comment = findByIdOrThrow(id);
		return mapToDto(comment);
	}

	public void deleteComment(Long id) {
		repository.deleteById(id);
	}

	public Comment update(Long id, CommentDto updatedComment, String username) {
		Comment existingComment = findByIdOrThrow(id);
		if (!existingComment.getAuthor().getUsername().equals(username)) {
			throw new AccessDeniedException("You are not allowed to update this comment.");
		}
		existingComment.setContent(updatedComment.content());
		existingComment.setUpdatedAt(LocalDateTime.now());
		return repository.save(existingComment);
	}

	private CommentDto mapToDto(Comment comment) {
		String authorName = comment.getAuthor() != null ? comment.getAuthor().getUsername() : "Deleted user";
		return new CommentDto(comment.getId(), authorName, comment.getContent(), comment.getCreatedAt(),
				comment.getUpdatedAt(), comment.getPost().getId());
	}

}
