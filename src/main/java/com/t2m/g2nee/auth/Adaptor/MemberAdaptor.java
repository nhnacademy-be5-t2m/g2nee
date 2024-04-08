package com.t2m.g2nee.auth.Adaptor;

import com.t2m.g2nee.auth.config.GatewayConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberAdaptor {
    private final GatewayConfig gatewayConfig;
    private final RestTemplate restTemplate;


}
