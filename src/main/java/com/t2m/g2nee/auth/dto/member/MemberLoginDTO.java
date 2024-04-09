package com.t2m.g2nee.auth.dto.member;




import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * front에서 전달받은 Member로그인 정보
 */


@AllArgsConstructor
@Getter
public class MemberLoginDTO {
    String userename;
     String password;
}
