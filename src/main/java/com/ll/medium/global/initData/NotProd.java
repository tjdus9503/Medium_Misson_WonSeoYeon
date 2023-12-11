package com.ll.medium.global.initData;

import com.ll.medium.domain.article.article.service.ArticleService;
import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import java.util.stream.IntStream;

@Profile("!prod")
@Configuration
@RequiredArgsConstructor
public class NotProd {
    @Autowired
    @Lazy
    private NotProd self;
    private final MemberService memberService;
    private final ArticleService articleService;

    @Bean
    public ApplicationRunner initNotProd() {
        return args -> {
            self.work1();
        };
    }

    public void work1() {
        if (memberService.count() > 0) return;

        String member1_uname = memberService.join("user1", "1234", "aa@aa").getData().getUsername();
        String member2_uname = memberService.join("user2", "1234", "aa@aa").getData().getUsername();
        String member3_uname = memberService.join("user3", "1234", "aa@aa").getData().getUsername();

        if (articleService.count() > 0) return;

        Member member1 = memberService.findByUsername(member1_uname).get();
        Member member2 = memberService.findByUsername(member2_uname).get();
        Member member3 = memberService.findByUsername(member3_uname).get();

        IntStream.rangeClosed(1, 100).forEach(num -> articleService.write(member1, "제목 " + num, "내용 " + num, true));
        IntStream.rangeClosed(101, 200).forEach(num -> articleService.write(member2, "제목 " + num, "내용 " + num, true));
        IntStream.rangeClosed(201, 300).forEach(num -> articleService.write(member3, "제목 " + num, "내용 " + num, true));
    }
}
