package com.t2m.g2nee.auth.service.apiService;

import com.t2m.g2nee.auth.dto.member.MemberLoginDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * shop 과의 통신을 위한 service 입니다.
 *
 * @author : 정지은
 * @since : 1.0
 */
@Service
public class ShopApiService {
    @Value("${g2nee.gateway}")
    String gatewayUrl;
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;

    public ShopApiService() {
        this.restTemplate = new RestTemplate();
        this.headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    /**
     * 일치하는 회원의 정보인지 확인하는 메소드
     *
     * @param memberLoginDTO username, password 를 입력
     * @return 일치할 시 true 를 반환
     */
    public boolean login(MemberLoginDTO memberLoginDTO){
        HttpEntity<MemberLoginDTO> requestEntity = new HttpEntity<>(memberLoginDTO, headers);
        ResponseEntity<Boolean> response = restTemplate.exchange(
                gatewayUrl + "shop/member/login",
                HttpMethod.POST,
                requestEntity,
                Boolean.class
        );
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("로그인을 실패하였습니다.");
        }
        return response.getBody();
    }

}
