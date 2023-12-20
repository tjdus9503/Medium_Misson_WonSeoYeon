package com.ll.medium.global.initData;

import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Profile("!prod")
@Configuration
@RequiredArgsConstructor
public class NotProd {
    @Autowired
    @Lazy
    private NotProd self;
    private final MemberService memberService;
    private final PostService postService;

    @Bean
    public ApplicationRunner initNotProd() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        if (memberService.count() > 0) return;

        String member1_uname = memberService.join("user1", "1234", "aa@aa").getData().getUsername();
        String member2_uname = memberService.join("user2", "1234", "aa@aa").getData().getUsername();
        String member3_uname = memberService.join("user3", "1234", "aa@aa").getData().getUsername();

        if (postService.count() > 0) return;

        List<String> usernames = List.of(member1_uname, member2_uname, member3_uname);
        List<Member> members = usernames.stream().map(username -> memberService.findByUsername(username).get()).toList();

        IntStream.rangeClosed(1, 500).forEach(num -> {
            Member author = members.get(num % 3);
            postService.write(author, "제목 " + num, "내용 " + num, num % 2 != 0);
        });
    }
}
