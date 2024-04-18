package com.t2m.g2nee.auth.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class CorsMvcConfig implements WebMvcConfigurer {

    /**
     * 프론트에서 요청이 오는 주소 설정
     *
     * @author kimsuhyeon
     * @version 1.0
     */
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://133.186.208.183:8100/")
                .allowedOrigins("http://g2nee:g2nee@133.186.150.129:8761/eureka");


    }
}
