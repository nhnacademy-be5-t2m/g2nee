package com.t2m.g2nee.auth.jwt.util;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;


    public JWTUtil(@Value("${spring.jwt.secret}")String secret){

        this.secretKey =new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

        //키를 객체타입으로 만들어저장


    }
    // 토큰을 전달받아 Jwts.parser로 검증 진행
    public String getUsername(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username",String.class);
    }

    public String getRole(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role",String.class);
    }

    public String getCategory(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category",String.class);
    }

    public Boolean isExpired(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        //토큰 만료 시간이 현재 시간을 지났는지 검증
    }


    public String createJwt(String category,String username, String role, Long expiredMs){
        return Jwts.builder()
                .claim("category",category)
                .claim("username", username)
                .claim("role",role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+expiredMs))
                .signWith(secretKey)
                .compact();
    }



    // 토큰 생성
}

