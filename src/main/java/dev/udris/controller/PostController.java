package dev.udris.controller;

import dev.udris.dto.CommentDto;
import dev.udris.dto.post.CreatePostDto;
import dev.udris.dto.post.PostDto;
import dev.udris.dto.post.PostSummaryDto;
import dev.udris.entity.Comment;
import dev.udris.entity.Post;
import dev.udris.entity.User;
import dev.udris.oauth2.CustomUserPrincipal;
import dev.udris.service.CommentService;
import dev.udris.service.PostService;
import dev.udris.service.TagService;
import dev.udris.service.UserService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;
    private final UserService userService;

    public PostController(PostService postService, CommentService commentService, TagService tagService,
			UserService userService) {
		this.postService = postService;
		this.commentService = commentService;
		this.tagService = tagService;
		this.userService = userService;
	}

	@GetMapping()
    public String allPublishedPostsByAuthor(@AuthenticationPrincipal CustomUserPrincipal principal, Model model) {
        User author = userService.findUserByIdOrThrow(principal.getId());
        List<PostSummaryDto> postDto = postService.getPublishedPostsByAuthor(author);
        model.addAttribute("posts", postDto);
        return "posts/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new CreatePostDto());
        model.addAttribute("availableTags", tagService.findAll());
        return "posts/form";
    }

    @PostMapping
    public String createPost(@ModelAttribute CreatePostDto postDto, @AuthenticationPrincipal CustomUserPrincipal principal) {
    	User author = userService.findUserByIdOrThrow(principal.getId());
        postService.createPostWithTags(postDto, author);
        return "redirect:/users/profile";

    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        postService.deletePostById(id);
        return "redirect:/users/profile";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        PostDto postDto = postService.findPostDtoById(id);
        model.addAttribute("post", postDto);
        model.addAttribute("availableTags", tagService.findAll());
        return "posts/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute PostDto postDto, @AuthenticationPrincipal CustomUserPrincipal principal) {
        postService.updateWithTags(id, postDto, postDto.tags(), principal.getName());
        return "redirect:/users/profile";
    }

    @PostMapping("/{id}/publish")
    public String publishPost(@PathVariable Long id, @AuthenticationPrincipal CustomUserPrincipal principal) {
        postService.publishPost(id, principal.getName());
        return "redirect:/users/profile";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserPrincipal principal) {
        Post post = postService.findPostByIdOrThrow(id);

        if (principal == null || !post.getAuthor().getUsername().equals(principal.getName())) {
            if (post.getStatus() != dev.udris.entity.PostStatus.PUBLISHED) {
                throw new AccessDeniedException("This post is not published.");
            }
        }

        List<CommentDto> comments = commentService.findByPost(post);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);

        // Anonymous users see read-only view, authenticated users see full view with comments
        if (principal == null) {
            return "posts/view-public";
        }

        model.addAttribute("comment", new CommentDto(null, null, null, null, null, null));
        model.addAttribute("currentUsername", principal.getName());
        return "posts/view";
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id, @ModelAttribute("comment") CommentDto commentDto,
                             @AuthenticationPrincipal CustomUserPrincipal principal) {
        Post post = postService.findPostByIdOrThrow(id);
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(commentDto.content());
        if (principal != null) {
        	User user = userService.findUserByIdOrThrow(principal.getId());
            comment.setAuthor(user);
            comment.setName(user.getUsername());
        }
        commentService.createComment(comment);
        return "redirect:/posts/" + id;
    }
}
