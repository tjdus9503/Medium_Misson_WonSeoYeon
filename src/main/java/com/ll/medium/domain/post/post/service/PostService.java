package com.ll.medium.domain.post.post.service;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.repository.PostRepository;
import com.ll.medium.standard.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final MemberService memberService;
    private final PostRepository postRepository;

    // 글 작성
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

    // 최신 글 30개 (홈 화면)
    public Page<PostDto> findTop30ByIsPublishedTrue(int page) {

        Sort sort = Sort.by("createDate").descending();
        List<Post> postList = postRepository.findTop30ByIsPublishedTrue(sort); // 최신 30개 게시물을 조회

        // 최신 30개 게시물 중 요청한 페이지에 해당하는 게시물만 추출
        int start = page * 10;
        int end = Math.min((page + 1) * 10, postList.size());
        List<Post> postSubList = postList.subList(start, end);

        // List<Post>를 Page<Post>로 변환
        Page<Post> postPage = new PageImpl<>(postSubList, PageRequest.of(page, 10), postList.size());

        return postPage.map(PostDto::new);
    }

    // 글 목록
    public Page<PostDto> findByIsPublishedTrue(int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createDate").descending());

        Page<Post> postPage = postRepository.findByIsPublishedTrue(pageable);

        return postPage.map(PostDto::new);
    }

    // 작성자의 글 모아보기 (+ 내 글 목록)
    public Page<PostDto> findByAuthor(User reqUser, String authorUsername, int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createDate").descending());

        Optional<Member> authorOptional = memberService.findByUsername(authorUsername);

        if (authorOptional.isEmpty()) {
            throw new RuntimeException("해당 작성자는 존재하지 않습니다.");
        }
        // 로그인 하지 않았거나, 작성자가 아닌 경우 : 공개글만 조회
        else if (reqUser == null || !reqUser.getUsername().equals(authorUsername)) {
            Page<Post> postPage = postRepository.findByAuthorAndIsPublishedTrue(authorOptional.get(), pageable);
            return postPage.map(PostDto::new);
        }
        // 작성자인 경우 : 비공개 포함한 모든 글 조회
        else {
            Member author = authorOptional.get();
            Page<Post> postPage = postRepository.findByAuthor(author, pageable);
            return postPage.map(PostDto::new);
        }
    }

    // 글 상세보기
    public PostDto findById(long id, User reqUser) {

        Optional<Post> postOptional = postRepository.findById(id);

        // 조건1 [필수 true] : 글이 존재해야한다.
        // 조건2 [둘 중 하나 true] : (1)공개글
        // 조건3 [둘 중 하나 true] : (2)요청자와 작성자가 동일
        if (postOptional.isPresent() &&
                (postOptional.get().isPublished() ||
                        (reqUser != null && reqUser.getUsername().equals(postOptional.get().getAuthor().getUsername())))
        ) {
            return new PostDto(postOptional.get());
        }
        // (조건 1 = false) 글이 존재하지 않는 경우
        // (조건 2 & 3 = false) 비공개글을 다른 유저가 요청하는 경우
        // => 두 경우에 동일한 메시지를 클라이언트에게 전달한다. (비공개 여부를 노출하지 않는다.)
        else {
            throw new DataNotFoundException("해당 글이 존재하지 않습니다.");
        }
    }

    // 글 수정 요청
    public PostDto findByIdAndCheckAuthor(long id, Member reqUser) {

        Optional<PostDto> postDtoOptional = postRepository.findById(id).map(PostDto::new);

        // 예외 처리 1 : 글이 존재하지 않는 경우
        if (postDtoOptional.isEmpty()) {
            throw new RuntimeException("해당 글이 존재하지 않습니다.");
        }

        PostDto postDto = postDtoOptional.get();

        // 예외 처리 2 : 요청자가 작성자가 아닌 경우
        if (!reqUser.getUsername().equals(postDto.getAuthorUsername())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        return postDto;
    }

    // 글 수정 처리
    @Transactional
    public PostDto modify(Member reqUser, long id, String title, String content, boolean isPublished) {

        Optional<Post> postOptional = postRepository.findById(id);

        // 예외 처리 1 : 글이 존재하지 않는 경우
        if (postOptional.isEmpty()) {
            throw new RuntimeException("해당 글이 존재하지 않습니다.");
        }

        Post post = postOptional.get();
        String authorUsername = post.getAuthor().getUsername();

        // 예외 처리 2 : 요청자가 작성자가 아닌 경우
        if (!reqUser.getUsername().equals(authorUsername)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        post.setTitle(title);
        post.setContent(content);
        post.setPublished(isPublished);

        return new PostDto(post);
    }

    // 글 삭제
    @Transactional
    public void delete(Member reqUser, long id) {
        
        Optional<Post> postOptional = postRepository.findById(id);

        // 예외 처리 1 : 글이 존재하지 않는 경우
        if (postOptional.isEmpty()) {
            throw new RuntimeException("해당 글이 존재하지 않습니다.");
        }

        Post post = postOptional.get();
        String authorUsername = post.getAuthor().getUsername();

        // 예외 처리 2 : 요청자가 작성자가 아닌 경우
        if (!reqUser.getUsername().equals(authorUsername)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }
}
