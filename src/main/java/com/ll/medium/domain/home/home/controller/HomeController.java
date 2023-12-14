package com.ll.medium.domain.home.home.controller;

import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import com.ll.medium.global.rsData.RsData;
import com.ll.medium.standard.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final PostService postService;
    private final Rq rq;

    @GetMapping("/")
    public String showMain(Model model) {
        List<PostDto> posts = postService.findTop30ByIsPublishedTrue();

        model.addAttribute("posts", posts);

        return "/domain/home/home/home";
    }

    @GetMapping("/b/{authorUsername}")
    public String list(Model model, @PathVariable String authorUsername, @RequestParam(defaultValue = "1") int page) {
        RsData<Page<PostDto>> rsPaging = postService.findByAuthor(rq.getUser(), authorUsername, page - 1);

        if (rsPaging.isFail()) {
            return rq.historyBack(rsPaging.getMsg());
        }
        else {
            model.addAttribute("authorUsername", authorUsername);
            model.addAttribute("paging", rsPaging.getData());

            return "/domain/home/home/authorPostList";
        }
    }

    @GetMapping("/b/{authorUsername}/{postId}")
    public String detail(Model model, @PathVariable long postId) {
        RsData<PostDto> rsPostDto = postService.findById(postId, rq.getUser());

        if (rsPostDto.isFail()) {
            return rq.historyBack(new DataNotFoundException(rsPostDto.getMsg()));
        }
        else {
            model.addAttribute("post", rsPostDto.getData());

            return "/domain/post/post/detail";
        }
    }
}
