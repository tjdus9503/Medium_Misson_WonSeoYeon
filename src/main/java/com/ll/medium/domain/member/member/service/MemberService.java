package com.ll.medium.domain.member.member.service;

import com.ll.medium.domain.member.member.dto.MemberDto;
import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<MemberDto> findDtoByUsername(String username) {
        Optional<Member> memberOptional = this.findByUsername(username);

        return memberOptional.map(MemberDto::new);
    }

    @Transactional
    public RsData<MemberDto> join(String username, String password, String email) {
        if (findByUsername(username).isPresent()) {
            return new RsData<>("400", "이미 사용중인 아이디입니다.");
        }

        Member member = Member
                .builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();

        memberRepository.save(member);

        return new RsData<>(
                "200",
                "%s님 환영합니다. 로그인을 해보세요.".formatted(member.getUsername()),
                new MemberDto(member)
        );
    }
}
