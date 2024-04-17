package com.t2m.g2nee.auth.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConstructorBinding
@RequiredArgsConstructor
@Component
@ConfigurationProperties(prefix="g2nee.redis")
public class RedisProperties {

    @Value("${host}")
    private String host;

    @Value("${port}")
    private int port;

    @Value("${password}")
    private String password;

    @Value("${database}")
    private int database;


}

