package com.t2m.g2nee.auth.Adaptor;

import com.t2m.g2nee.auth.config.GatewayConfig;
import com.t2m.g2nee.auth.dto.member.MemberInfoRequestDTO;
import com.t2m.g2nee.auth.dto.member.MemberInfoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//shop에 member정보 요청
@Slf4j
@Component
@AllArgsConstructor
public class MemberAdaptor {
    private final GatewayConfig gateWayConfig;
    private final RestTemplate restTemplate;

    /**
     * shop 서버에 회원정보를 요청하는 메소드.
     *
     * @param requestDto 멤버 아이디 정보가 들어있는 회원정보 요청 dto
     * @return 회원정보를 응답받아 리턴해준다.
     */
    public ResponseEntity<MemberInfoResponseDTO> loginRequest(
            MemberInfoRequestDTO requestDto) {

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MemberInfoRequestDTO> entity = new HttpEntity<>(requestDto, headers);

        return restTemplate.exchange(
                gateWayConfig.getGatewayUrl() + "/login",
                HttpMethod.POST,
                entity,
                MemberInfoResponseDTO.class
        );
    }
}