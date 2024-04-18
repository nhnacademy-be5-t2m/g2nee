package com.t2m.g2nee.auth.dto.member;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;


/**
 * 토큰 반환 DTO클래스
 *
 * @author 김수현
 *
 */
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
public class MemberInfoResponseDTO {
    private Long memberId;
    private String username;
    private String password;
    private List<String> authorities;


}
