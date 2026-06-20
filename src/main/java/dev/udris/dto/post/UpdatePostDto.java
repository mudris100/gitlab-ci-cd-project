package dev.udris.dto.post;

import java.time.LocalDateTime;

import dev.udris.entity.PostStatus;

public record UpdatePostDto(Long id, String title, String content, LocalDateTime updatedOn, PostStatus status) {

}
