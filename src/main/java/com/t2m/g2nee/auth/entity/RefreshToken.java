package com.t2m.g2nee.auth.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@Getter
@Setter
@RedisHash(value = "refreshToken", timeToLive = 86400)
public class RefreshToken {

    @Id
    private String refreshToken;


    private String username;


    private String accessToken;

    private String expiration;


}
