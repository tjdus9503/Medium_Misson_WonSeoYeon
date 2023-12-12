package com.ll.medium.domain.post.post.service;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public void write(Member author, String title, String body, boolean isPublished) {
        Post post = Post.builder()
                .author(author)
                .title(title)
                .body(body)
                .isPublished(isPublished)
                .build();

        postRepository.save(post);
    }

    public long count() {
        return postRepository.count();
    }

    public List<PostDto> getRecentPublishedPosts() {
        Sort sort = Sort.by("createDate").descending();
        List<Post> Posts = postRepository.findTop30ByIsPublishedTrue(sort);

        return Posts.stream()
                .map(PostDto::new)
                .toList();
    }

    public Page<PostDto> getPublishedPosts(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createDate").descending());

        Page<Post> postPage = postRepository.findByIsPublishedTrue(pageable);

        return postPage.map(PostDto::new);
    }

    public Page<PostDto> getMyPosts(Member member, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createDate").descending());

        Page<Post> postPage = postRepository.findByAuthor(member, pageable);

        return postPage.map(PostDto::new);
    }
}
