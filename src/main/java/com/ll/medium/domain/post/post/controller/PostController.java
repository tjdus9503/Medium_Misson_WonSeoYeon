package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.form.WriteForm;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import com.ll.medium.standard.exception.DataNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final Rq rq;

    @GetMapping("/list")
    public String list (Model model, @RequestParam(defaultValue = "1") int page) {
        Page<PostDto> paging = postService.getPublishedPosts(page - 1);

        model.addAttribute("paging", paging);

        return "/domain/post/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myList")
    public String myList (Model model, @RequestParam(defaultValue = "1") int page) {
        Page<PostDto> paging = postService.getMyPosts(rq.getMember(), page - 1);

        model.addAttribute("paging", paging);

        return "/domain/post/post/myList";
    }

    @GetMapping("/{id}")
    public String detail (Model model, @PathVariable("id") long id) {
        Optional<PostDto> postDtoOptional = postService.findById(id);

        // 조건1 [필수 true] : 글이 존재해야한다.
        // 조건2 [둘 중 하나 true] : (1)공개글
        // 조건3 [둘 중 하나 true] : (2)요청자와 작성자가 동일
        if (postDtoOptional.isPresent() &&
                (postDtoOptional.get().isPublished() ||
                rq.getUser().getUsername().equals(postDtoOptional.get().getAuthorUsername()))
        ) {
            model.addAttribute("post", postDtoOptional.get());

            return "/domain/post/post/detail";
        }
        // (조건 1 = false) 글이 존재하지 않는 경우
        // (조건 2 & 3 = false) 비공개글을 다른 유저가 요청하는 경우
        // 두 경우에 동일한 메시지를 클라이언트에게 전달한다. (비공개 여부도 노출하지 않는다.)
        else {
            return rq.historyBack(new DataNotFoundException("해당 글이 존재하지 않습니다"));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String write () {
        return "/domain/post/post/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write (@Valid WriteForm writeForm) {
        boolean isPublished = writeForm.getIsPublished() != null && writeForm.getIsPublished().equals("true");
        PostDto postDto = postService.write(rq.getMember(), writeForm.getTitle(), writeForm.getContent(), isPublished);

        return rq.redirect("/post/myList", "%d번 게시물 생성되었습니다.".formatted(postDto.getId()));
    }
}
