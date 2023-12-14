package com.ll.medium.domain.post.post.service;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.repository.PostRepository;
import com.ll.medium.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public PostDto write(Member author, String title, String content, boolean isPublished) {
        Post post = Post.builder()
                .author(author)
                .title(title)
                .content(content)
                .isPublished(isPublished)
                .build();

        postRepository.save(post);

        return new PostDto(post);
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

    public Optional<PostDto> findById(long id) {
        Optional<Post> postOptional = postRepository.findById(id);

        return postOptional.map(PostDto::new);
    }

    public RsData<PostDto> findByIdAndCheckAuthor(long id, Member reqUser) {
        Optional<PostDto> postDtoOptional = findById(id);

        // 예외 처리 1 : 글이 존재하지 않는 경우
        if (postDtoOptional.isEmpty()) {
            return new RsData<>("400", "해당 글이 존재하지 않습니다.");
        }

        PostDto postDto = postDtoOptional.get();

        // 예외 처리 2 : 요청자가 작성자가 아닌 경우
        if (!reqUser.getUsername().equals(postDto.getAuthorUsername())) {
            return new RsData<>("400", "수정 권한이 없습니다.");
        }

        return new RsData<>("200", "", postDto);
    }

    @Transactional
    public RsData<PostDto> modify(Member reqUser, long id, String title, String content, boolean isPublished) {
        Optional<Post> postOptional = postRepository.findById(id);

        // 예외 처리 1 : 글이 존재하지 않는 경우
        if (postOptional.isEmpty()) {
            return new RsData<>("400", "해당 글이 존재하지 않습니다.");
        }

        Post post = postOptional.get();
        String authorUsername = post.getAuthor().getUsername();

        // 예외 처리 2 : 요청자가 작성자가 아닌 경우
        if (!reqUser.getUsername().equals(authorUsername)) {
            return new RsData<>("400", "수정 권한이 없습니다.");
        }

        post.setTitle(title);
        post.setContent(content);
        post.setPublished(isPublished);

        return new RsData<>("200", "%d번 글이 수정되었습니다.".formatted(id), new PostDto(post));
    }

    @Transactional
    public RsData<PostDto> delete(Member reqUser, long id) {
        Optional<Post> postOptional = postRepository.findById(id);

        // 예외 처리 1 : 글이 존재하지 않는 경우
        if (postOptional.isEmpty()) {
            return new RsData<>("400", "해당 글이 존재하지 않습니다.");
        }

        Post post = postOptional.get();
        String authorUsername = post.getAuthor().getUsername();

        // 예외 처리 2 : 요청자가 작성자가 아닌 경우
        if (!reqUser.getUsername().equals(authorUsername)) {
            return new RsData<>("400", "삭제 권한이 없습니다.");
        }

        postRepository.delete(post);

        return new RsData<>("200", "%d번 글이 삭제되었습니다.".formatted(id));
    }
}
