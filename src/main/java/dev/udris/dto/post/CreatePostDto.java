package dev.udris.dto.post;

import dev.udris.entity.PostStatus;

public class CreatePostDto {
    private String title;
    private String content;
    private PostStatus status = PostStatus.DRAFT;
    private String tags;

    public CreatePostDto() {
    }

    public CreatePostDto(String title, String content, PostStatus status, String tags) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
} 