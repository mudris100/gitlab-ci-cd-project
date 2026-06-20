package dev.udris.controller;

import dev.udris.dto.post.PostDto;
import dev.udris.entity.User;
import dev.udris.oauth2.CustomUserPrincipal;
import dev.udris.service.PostService;
import dev.udris.service.UserService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final PostService postService;
    private final UserService userService;

    public UserController(PostService postService, UserService userService) {
		this.postService = postService;
		this.userService = userService;
	}


	@GetMapping("/profile")
    public String userProfile(Model model, @AuthenticationPrincipal CustomUserPrincipal principal) {
      User user = userService.findUserByIdOrThrow(principal.getId());
        List<PostDto> posts = postService.findByAuthor(user);
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        return "user/profile";
    }
}
