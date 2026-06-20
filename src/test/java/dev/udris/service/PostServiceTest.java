package dev.udris.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dev.udris.dto.post.PostSummaryDto;
import dev.udris.dto.post.UpdatePostDto;
import dev.udris.entity.Post;
import dev.udris.entity.PostStatus;
import dev.udris.entity.User;
import dev.udris.mapper.PostMapper;
import dev.udris.repository.PostRepository;
import dev.udris.service.CommentService;
import dev.udris.service.PostService;

class PostServiceTest {
	@Mock
	private PostRepository repository;
	@Mock
	private CommentService commentService;
	@Mock
	private PostMapper mapper;
	@InjectMocks
	private PostService postService;

	private Post post1;
	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		user = new User();
		user.setId(1);
		user.setUsername("testuser");
		post1 = new Post("Title", "Content", user);
		post1.setId(1L);
	}

	@Test
	void shouldCreatePost() {
		when(repository.save(any(Post.class))).thenReturn(post1);
		Post saved = postService.createPost(post1);
		assertNotNull(saved);
		assertEquals(post1.getTitle(), saved.getTitle());
	}

	@Test
	void shouldUpdateExistingPost() {
		UpdatePostDto updatePostDto = new UpdatePostDto(1L, "New Title", "New content", LocalDateTime.now(),
				PostStatus.PUBLISHED);
		when(repository.findById(1L)).thenReturn(Optional.of(post1));
		when(repository.save(any(Post.class))).thenReturn(post1);
		when(mapper.toUpdatePostDto(any(Post.class))).thenReturn(updatePostDto);
		Post updated = new Post("New Title", "New Content", user);
		UpdatePostDto result = postService.update(1L, updated);
		assertEquals("New Title", result.title());
		assertThat(result.content()).isEqualTo("New content");
		assertThat(result.status()).isEqualTo(PostStatus.PUBLISHED);

	}

	@Test
	void shouldFindAllPublishedPosts() {
		Post post2 = new Post("MyTitle", "NewContent", user);
		List<Post> posts = List.of(post1, post2);
		when(repository.findByStatusOrderByPublishedOnDesc(PostStatus.PUBLISHED)).thenReturn(posts);
		PostSummaryDto dto1 = new PostSummaryDto();
		PostSummaryDto dto2 = new PostSummaryDto();
		dto1.setAuthorName(post1.getAuthor().getUsername());
		dto1.setTitle(post1.getTitle());
		dto2.setAuthorName(post2.getAuthor().getUsername());
		dto2.setTitle(post2.getTitle());
		when(mapper.toSummaryDto(post1)).thenReturn(dto1);
		when(mapper.toSummaryDto(post2)).thenReturn(dto2);
		// Act
		List<PostSummaryDto> result = postService.findAllPublished();
		// Assert
		assertEquals(2, result.size());
		assertEquals("Title", dto1.getTitle());
		assertTrue(result.contains(dto1));
		assertTrue(result.contains(dto2));

		verify(repository).findByStatusOrderByPublishedOnDesc(PostStatus.PUBLISHED);
		verify(mapper).toSummaryDto(post1);
		verify(mapper).toSummaryDto(post2);

	}

}