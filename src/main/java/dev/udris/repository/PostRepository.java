package dev.udris.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.udris.entity.Post;
import dev.udris.entity.PostStatus;
import dev.udris.entity.Tag;
import dev.udris.entity.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findByAuthor(User author);
	List<Post> findByStatus(PostStatus status);
	List<Post> findByAuthorAndStatus(User author, PostStatus status);
	List<Post> findByStatusOrderByPublishedOnDesc(PostStatus status);
	List<Post> findByTagsContaining(Tag tag);
	List<Post> findByStatusAndTagsContaining(PostStatus status, Tag tag);
}
