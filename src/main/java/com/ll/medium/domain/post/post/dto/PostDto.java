package com.ll.medium.domain.post.post.dto;

import com.ll.medium.domain.post.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private boolean isPublished;

    public PostDto(Post post) {
        id = post.getId();
        title = post.getTitle();
        content = post.getContent();
        authorUsername = post.getAuthor().getUsername();
        createDate = post.getCreateDate();
        modifyDate = post.getModifyDate();
        isPublished = post.isPublished();
    }
}
