package com.t2m.g2nee.auth.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@NoArgsConstructor
@RedisHash(value = "refreshToken",timeToLive = 86400)
public class RefreshToken {
    @Id
    private String refreshToken;

    private String username;


    private String expiration;










}
