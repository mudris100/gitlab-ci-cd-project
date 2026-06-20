package dev.udris.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.udris.dto.post.PostDto;
import dev.udris.entity.PostStatus;
import dev.udris.entity.Tag;
import dev.udris.service.PostService;
import dev.udris.service.TagService;

@Controller
@RequestMapping("/tags")
public class TagController {

	private final TagService tagService;
	private final PostService postService;

	public TagController(TagService tagService, PostService postService) {
		this.tagService = tagService;
		this.postService = postService;
	}

	@GetMapping("/{name}")
	public String findByName(@PathVariable String name, Model model) {
		Tag tag = tagService.findByName(name);
		List<PostDto> postDtos = postService.getPublishedPostsByTag(PostStatus.PUBLISHED, tag);
		model.addAttribute("tag", tag.getName());
		model.addAttribute("posts", postDtos);
		return "tags/posts-by-tag";

	}

}
