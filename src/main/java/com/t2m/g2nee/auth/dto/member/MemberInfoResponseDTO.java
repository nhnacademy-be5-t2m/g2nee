package com.t2m.g2nee.auth.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 토큰 반환 DTO클래스
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoResponseDTO {
    private Long memberId;
    private String username;
    private String password;
    private List<String> authorities;



}
