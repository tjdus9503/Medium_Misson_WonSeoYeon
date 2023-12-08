package com.ll.medium.domain.member.member.dto;

import com.ll.medium.domain.member.member.entity.Member;
import lombok.Getter;

@Getter
public class MemberDto {
    private Long id;
    private String username;
    private String email;

    public MemberDto (Member member) {
        id = member.getId();
        username = member.getUsername();
        email = member.getEmail();
    }
}
