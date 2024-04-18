package com.t2m.g2nee.auth.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * shop에서 멤버 정보 받아올 DTO
 */

@Getter
@AllArgsConstructor
public class MemberInfoRequestDTO {

    private String username;

}
