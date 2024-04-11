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


@Slf4j
@Component
@AllArgsConstructor
public class MemberAdaptor {
    private final GatewayConfig gateWayConfig;
    private final RestTemplate restTemplate;

    /**
     * shop에 Member정보 요청해 username이 담긴 MemberInfoRequest에 담음
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

    public ResponseEntity<Boolean> login(MemberLoginDTO memberLoginDTO){
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MemberLoginDTO> entity = new HttpEntity<>(memberLoginDTO,headers);

        return restTemplate.exchange(
                gateWayConfig.getGatewayUrl() + "shop/member/login",
                HttpMethod.POST,
                entity,
                Boolean.class
        );
    }
}