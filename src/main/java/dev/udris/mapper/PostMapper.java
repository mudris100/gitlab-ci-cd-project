package dev.udris.mapper;

import dev.udris.dto.CommentDto;
import dev.udris.dto.post.CreatePostDto;
import dev.udris.dto.post.PostDto;
import dev.udris.dto.post.PostSummaryDto;
import dev.udris.dto.post.UpdatePostDto;
import dev.udris.entity.Post;
import dev.udris.entity.Tag;
import dev.udris.service.CommentService;
import dev.udris.service.TagService;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {

    public Post toEntity(CreatePostDto dto) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setStatus(dto.getStatus());
        return post;
    }

    public PostSummaryDto toSummaryDto(Post post) {
        PostSummaryDto dto = new PostSummaryDto();
        dto.setPostId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthorName(post.getAuthor().getUsername());
        dto.setPublishedOn(post.getPublishedOn());
        dto.setTags(post.getTags().stream().map(Tag::getName).collect(Collectors.joining(", ")));
        return dto;
    }

    public UpdatePostDto toUpdatePostDto(Post post) {
        return new UpdatePostDto(post.getId(), post.getTitle(), post.getContent(), post.getUpdatedOn(), post.getStatus());
    }

    public PostDto toPostDto(Post post, CommentService commentService, TagService tagService) {
        List<CommentDto> comments = commentService.findByPost(post);
        String tagsString = tagService.tagsToString(post.getTags());
        return new PostDto(post.getId(), post.getTitle(), post.getContent(), post.getPublishedOn(), post.getStatus(),
                tagsString, comments);
    }

}
