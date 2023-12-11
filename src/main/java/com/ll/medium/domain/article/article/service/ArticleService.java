package com.ll.medium.domain.article.article.service;

import com.ll.medium.domain.article.article.entity.Article;
import com.ll.medium.domain.article.article.repository.ArticleRepository;
import com.ll.medium.domain.member.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository articleRepository;

    @Transactional
    public void write(Member author, String title, String body, boolean isPublished) {
        Article article = Article.builder()
                .author(author)
                .title(title)
                .body(body)
                .isPublished(isPublished)
                .build();

        articleRepository.save(article);
    }

    public long count() {
        return articleRepository.count();
    }
}
