package com.t2m.g2nee.auth.adaptor;

import com.t2m.g2nee.auth.config.GatewayConfig;
import com.t2m.g2nee.auth.dto.member.MemberInfoRequestDTO;
import com.t2m.g2nee.auth.dto.member.MemberInfoResponseDTO;
import com.t2m.g2nee.auth.dto.member.MemberLoginDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Shop Server와 멤버정보 관련해 통신하기위한 클래스
 *
 * @author 김수현
 * @version 1.0
 */
@Slf4j
@Component
@AllArgsConstructor
public class MemberAdaptor {
    private final GatewayConfig gateWayConfig;
    private final RestTemplate restTemplate;


    /**
     * Shop Server에 Member정보 요청하는 메소드
     * @param requestDto username이 들어있는 회원정보 요청 DTO
     * @return Id,username,password,권한 등의 회원정보를 응답받아 리턴해준다
     */

    public ResponseEntity<MemberInfoResponseDTO> loginRequest(
            MemberInfoRequestDTO requestDto) {

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MemberInfoRequestDTO> entity = new HttpEntity<>(requestDto, headers);

        return restTemplate.exchange(
                gateWayConfig.getGatewayUrl() + "shop/member/getInfo",
                HttpMethod.POST,
                entity,
                MemberInfoResponseDTO.class
        );
    }

    public ResponseEntity<Boolean> login(MemberLoginDTO memberLoginDTO) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MemberLoginDTO> entity = new HttpEntity<>(memberLoginDTO, headers);

        return restTemplate.exchange(
                gateWayConfig.getGatewayUrl() + "shop/member/login",
                HttpMethod.POST,
                entity,
                Boolean.class
        );
    }
}