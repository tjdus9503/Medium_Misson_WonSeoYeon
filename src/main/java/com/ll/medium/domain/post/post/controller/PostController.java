package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.form.WriteForm;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import com.ll.medium.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final Rq rq;

    @GetMapping("/list")
    public String list (Model model, @RequestParam(defaultValue = "1") int page) {

        Page<PostDto> paging = postService.findByIsPublishedTrue(page - 1);

        model.addAttribute("paging", paging);

        return "/domain/post/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myList")
    public String myList (Model model, @RequestParam(defaultValue = "1") int page) {

        RsData<Page<PostDto>> rsPaging = postService.findByAuthor(rq.getUser(), rq.getMember().getUsername(), page - 1);

        model.addAttribute("paging", rsPaging.getData());

        return "/domain/post/post/myList";
    }

    @GetMapping("/{id}")
    public String detail (Model model, @PathVariable("id") long id) {

        RsData<PostDto> rsPostDto = postService.findById(id, rq.getUser());

        model.addAttribute("post", rsPostDto.getData());

        return "/domain/post/post/detail";

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String write () {
        return "/domain/post/post/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write (@Valid WriteForm writeForm) {
        // 체크박스가 체크되지 않았을 때, false로 설정
        boolean isPublished = writeForm.getIsPublished() != null && writeForm.getIsPublished();

        PostDto postDto = postService.write(rq.getMember(), writeForm.getTitle(), writeForm.getContent(), isPublished);

        return rq.redirect("/post/myList", "%d번 게시물 생성되었습니다.".formatted(postDto.getId()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String modify (@PathVariable("id") int id, Model model) {

        RsData<PostDto> rsPostDto = postService.findByIdAndCheckAuthor(id, rq.getMember());

        model.addAttribute("post", rsPostDto.getData());

        return "/domain/post/post/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/modify")
    public String modify (@PathVariable("id") long id, @Valid WriteForm writeForm) {
        // 체크박스가 체크되지 않았을 때, false로 설정
        boolean isPublished = writeForm.getIsPublished() != null && writeForm.getIsPublished();

        RsData<PostDto> rsPostDto = postService.modify(rq.getMember(), id, writeForm.getTitle(), writeForm.getContent(), isPublished);

        return rq.redirect("/post/myList", rsPostDto.getMsg());
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/delete")
    public String delete (@PathVariable("id") long id) {

        RsData<PostDto> rsPostDto = postService.delete(rq.getMember(), id);

        return rq.redirect("/post/myList", rsPostDto.getMsg());
    }
}
