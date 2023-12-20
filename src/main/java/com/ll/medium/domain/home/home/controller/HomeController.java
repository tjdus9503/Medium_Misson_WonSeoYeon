package com.ll.medium.domain.home.home.controller;


import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class HomeController {
    private final PostService postService;
    private final Rq rq;

    @GetMapping("/")
    public String showMain(Model model, @RequestParam(defaultValue = "1") int page) {

        Page<PostDto> paging = postService.findTop30ByIsPublishedTrue(page - 1);

        model.addAttribute("paging", paging);

        return "/domain/home/home/home";
    }
}
