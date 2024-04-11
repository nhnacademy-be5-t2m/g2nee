package com.t2m.g2nee.auth.jwt.util;

import com.t2m.g2nee.auth.entity.RefreshToken;
import com.t2m.g2nee.auth.repository.RefreshTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AddRefreshTokenUtil {

    private RefreshTokenRepository refreshTokenRepository;

    public AddRefreshTokenUtil(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public static void addRefreshEntity(RefreshTokenRepository refreshTokenRepository, String username, String refresh,
                                        String access, Long expiredMs) {


        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUsername(username);
        refreshTokenEntity.setExpiration(date.toString());
        refreshTokenEntity.setRefreshToken(refresh);
        refreshTokenEntity.setAccessToken(access);

        refreshTokenRepository.save(refreshTokenEntity);
    }
}
