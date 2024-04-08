package com.t2m.g2nee.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//Gateway와 연동
@Configuration
@ConfigurationProperties(prefix = "g2nee")
public class GatewayConfig {

    private String gateway;
    public  String getGatewayUrl(){
        return gateway;
    }

    public void setGateway(String gateway){
        this.gateway=gateway;
    }
}

