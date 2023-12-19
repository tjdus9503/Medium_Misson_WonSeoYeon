package com.ll.medium.domain.member.member.controller;

import com.ll.medium.domain.member.member.dto.MemberDto;
import com.ll.medium.domain.member.member.form.JoinForm;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.global.rq.Rq;
import com.ll.medium.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final Rq rq;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String join () {
        return "/domain/member/member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join (@Valid JoinForm joinForm) {
        if (!joinForm.getPassword().equals(joinForm.getPasswordConfirm())) {
            rq.historyBack("msg");
        }

        RsData<MemberDto> joinRs = memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getEmail());

        return rq.redirectOrBack("/member/login", joinRs);
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login () {
        return "/domain/member/member/login";
    }
}
