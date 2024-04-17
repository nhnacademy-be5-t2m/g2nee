package com.t2m.g2nee.auth.dto.member;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * front에서 전달받은 Member로그인 정보
 */


@RequiredArgsConstructor
@Getter
@Setter
public class MemberLoginDTO {
    private String username;
    private String password;
}
