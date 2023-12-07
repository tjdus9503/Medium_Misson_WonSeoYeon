package com.ll.medium.domain.member.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDto {
    private Long id;
    private String username;
    private String email;
}
