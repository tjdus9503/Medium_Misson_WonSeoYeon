package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
