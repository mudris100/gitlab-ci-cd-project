package dev.udris.service;

import dev.udris.dto.CommentDto;
import dev.udris.dto.post.CreatePostDto;
import dev.udris.dto.post.PostDto;
import dev.udris.dto.post.PostSummaryDto;
import dev.udris.dto.post.UpdatePostDto;
import dev.udris.entity.Post;
import dev.udris.entity.PostStatus;
import dev.udris.entity.Tag;
import dev.udris.entity.User;
import dev.udris.mapper.PostMapper;
import dev.udris.repository.PostRepository;
import dev.udris.service.kafka.NewPostEvent;
import dev.udris.service.kafka.PostEventProducer;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository repository;
    private final CommentService commentService;
    private final TagService tagService;
    private final PostMapper mapper;
    private final ObjectProvider<PostEventProducer> postEventProducer;

    public PostService(PostRepository repository, CommentService commentService, TagService tagService, PostMapper mapper, ObjectProvider<PostEventProducer> postEventProducer) {
        this.repository = repository;
        this.commentService = commentService;
        this.tagService = tagService;
        this.mapper = mapper;
        this.postEventProducer = postEventProducer;
    }

    public long count() {
        return repository.count();
    }

    public List<PostDto> findByAuthor(User author) {
        return repository.findByAuthor(author).stream().map(p -> mapper.toPostDto(p, commentService, tagService)).collect(Collectors.toList());
    }

    public void deletePostById(Long id) {
        repository.deleteById(id);
    }

    public List<PostSummaryDto> getPublishedPostsByAuthor(User author) {
        List<Post> posts = repository.findByAuthorAndStatus(author, PostStatus.PUBLISHED);
        return posts.stream().map(mapper::toSummaryDto).collect(Collectors.toList());
    }

    public List<PostSummaryDto> findAllPublished() {
        List<Post> posts = repository.findByStatusOrderByPublishedOnDesc(PostStatus.PUBLISHED);
        return posts.stream().map(mapper::toSummaryDto).collect(Collectors.toList());
    }

    public PostDto findPostDtoById(Long id) {
        Post post = findPostByIdOrThrow(id);
        return mapper.toPostDto(post, commentService, tagService);
    }

    public PostDto findPublishedById(Long id) {
        Post post = findPostByIdOrThrow(id);
        if (post.getStatus() != PostStatus.PUBLISHED) {
            throw new dev.udris.exception.NotFoundException("Post", "id", id);
        }
        return mapper.toPostDto(post, commentService, tagService);
    }

    public Post findPostByIdOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new dev.udris.exception.NotFoundException("Post", "id", id));
    }

    public Post createPost(Post post) {
        return repository.save(post);
    }

    public Post createPostWithTags(CreatePostDto postDto, User author) {
        Post post = mapper.toEntity(postDto);
        post.setAuthor(author);
        Set<Tag> tags = tagService.parseTagsFromString(postDto.getTags());
        post.setTags(tags);
        repository.save(post);
        if (post.getStatus().equals(PostStatus.PUBLISHED)) {
            postEventProducer.ifAvailable(postEvent -> postEvent.sendNewPostEvent
                    (new NewPostEvent(post.getId(), post.getTitle(), post.getAuthor().getUsername())));
        }
        return post;

    }

    public UpdatePostDto update(Long id, Post updatedPost) {
        Post existingPost = findPostByIdOrThrow(id);
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setStatus(updatedPost.getStatus());
        existingPost.setUpdatedOn(LocalDateTime.now());
        Post post = repository.save(existingPost);
        return mapper.toUpdatePostDto(post);
    }

    public Post updateWithTags(Long id, PostDto updatedPost, String tagsString, String username) {
        Post existingPost = findPostByIdOrThrow(id);
        if (!existingPost.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to update this post.");
        }
        existingPost.setTitle(updatedPost.title());
        existingPost.setContent(updatedPost.content());
        existingPost.setStatus(updatedPost.status());
        existingPost.setUpdatedOn(LocalDateTime.now());

        Set<Tag> tags = tagService.parseTagsFromString(tagsString);
        existingPost.setTags(tags);

        return repository.save(existingPost);
    }

    public void publishPost(Long id, String username) {
        Post existingPost = findPostByIdOrThrow(id);
        if (!existingPost.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to publish this post.");
        }
        existingPost.setStatus(PostStatus.PUBLISHED);
        existingPost.setUpdatedOn(LocalDateTime.now());
        repository.save(existingPost);
        postEventProducer.ifAvailable(postEvent -> postEvent.sendNewPostEvent
                (new NewPostEvent(existingPost.getId(), existingPost.getTitle(), existingPost.getAuthor().getUsername())));
    }


    public List<PostDto> getPublishedPostsByTag(PostStatus postStatus, Tag tag) {
        List<Post> posts = repository.findByStatusAndTagsContaining(postStatus, tag);
        return posts.stream().map(p -> mapper.toPostDto(p, commentService, tagService)).collect(Collectors.toList());
    }

    public List<CommentDto> findCommentsByPost(Post post) {
        return commentService.findByPost(post);
    }
}
