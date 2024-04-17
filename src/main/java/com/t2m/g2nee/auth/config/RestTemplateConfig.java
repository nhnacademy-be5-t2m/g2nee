package com.t2m.g2nee.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Redis에 토큰을 저장하기 위한 설정
 *
 * @author kimsuhyeon
 * @version 1.0
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(3000);
        factory.setReadTimeout(100000);
        factory.setBufferRequestBody(false);

        return factory;

    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        return new RestTemplate(clientHttpRequestFactory);
    }

}
