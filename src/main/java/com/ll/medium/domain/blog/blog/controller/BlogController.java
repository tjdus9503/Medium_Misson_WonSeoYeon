package com.ll.medium.domain.blog.blog.controller;

import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/b")
public class BlogController {
    private final PostService postService;
    private final Rq rq;

    @GetMapping("/{authorUsername}")
    public String list(Model model, @PathVariable String authorUsername, @RequestParam(defaultValue = "1") int page) {

        Page<PostDto> postDtoPage = postService.findByAuthor(rq.getUser(), authorUsername, page - 1);

        model.addAttribute("authorUsername", authorUsername);
        model.addAttribute("paging", postDtoPage);

        return "/domain/home/home/authorPostList";
    }

    @GetMapping("/{authorUsername}/{postId}")
    public String detail(Model model, @PathVariable long postId) {

        PostDto postDto = postService.findById(postId, rq.getUser());

        model.addAttribute("post", postDto);

        return "/domain/post/post/detail";
    }
}
