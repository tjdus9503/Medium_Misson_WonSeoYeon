package com.ll.medium.domain.home.home.controller;

import com.ll.medium.domain.article.article.entity.Article;
import com.ll.medium.domain.article.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ArticleService articleService;

    @GetMapping("/")
    public String showMain(Model model) {
        List<Article> articles = articleService.getRecentPublishedArticles();

        System.out.println("articles 자료의 갯수 : " + articles.size());
        model.addAttribute("articles", articles);

        return "/domain/home/home/home";
    }
}
