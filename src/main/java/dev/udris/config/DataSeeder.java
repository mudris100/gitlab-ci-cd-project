package dev.udris.config;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.udris.entity.Post;
import dev.udris.entity.PostStatus;
import dev.udris.entity.User;
import dev.udris.service.PostService;
import dev.udris.service.UserService;

@Component
public class DataSeeder implements CommandLineRunner {

	private final UserService userService;
	private final PostService postService;

	@Value("${app.seed-data:true}")
    private boolean seedData;
	
	public DataSeeder(UserService userService, PostService postService) {
		this.userService = userService;
		this.postService = postService;
	}

	@Override
	public void run(String... args) throws Exception {
		if (!seedData) return;
		ObjectMapper mapper = new ObjectMapper();

		if (userService.findAllUsersAsDto().size() < 3) {
			InputStream inputStream = new ClassPathResource("users.json").getInputStream();

			List<User> users = mapper.readValue(inputStream, new TypeReference<List<User>>() {
			});

			userService.createAllUsers(users);
			System.out.println("✅ Users seeded successfully.");
		}

		if (postService.count() < 1) {
			InputStream postInput = new ClassPathResource("posts.json").getInputStream();
			List<PostDTO> postDtos = mapper.readValue(postInput, new TypeReference<List<PostDTO>>() {});

			for (PostDTO dto : postDtos) {
				User author = userService.findByUsernameOrThrow(dto.getAuthorUsername());

				Post post = new Post(dto.getTitle(), dto.getContent(),  PostStatus.PUBLISHED, author);
				postService.createPost(post);
			}
			System.out.println("✅ Posts seeded successfully.");
		}
	

		}
	
	//This is my inner static class used only during post deserialization
	private static class PostDTO {
		private String title;
		private String content;
		private String authorUsername;

		public String getTitle() {
			return title;
		}

		public String getContent() {
			return content;
		}

		public String getAuthorUsername() {
			return authorUsername;
		}

	}

}
