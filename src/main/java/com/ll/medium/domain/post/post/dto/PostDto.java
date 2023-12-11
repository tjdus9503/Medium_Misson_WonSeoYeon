package com.ll.medium.domain.post.post.dto;

import com.ll.medium.domain.post.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDto {
    private Long id;
    private String title;
    private String body;
    private String authorName;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private boolean isPublished;

    public PostDto(Post post) {
        id = post.getId();
        title = post.getTitle();
        body = post.getBody();
        authorName = post.getAuthor().getUsername();
        createDate = post.getCreateDate();
        modifyDate = post.getModifyDate();
        isPublished = post.isPublished();
    }
}
