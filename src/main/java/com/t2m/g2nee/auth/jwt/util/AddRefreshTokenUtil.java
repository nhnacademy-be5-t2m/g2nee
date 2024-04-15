package com.t2m.g2nee.auth.jwt.util;

import com.t2m.g2nee.auth.entity.RefreshToken;
import com.t2m.g2nee.auth.repository.RefreshTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AddRefreshTokenUtil {

    private final RefreshTokenRepository refreshTokenRepository;

    public AddRefreshTokenUtil(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public void addRefreshEntity(RefreshTokenRepository refreshTokenRepository, String username, String refresh,
                                        String access, Long expiredMs) {


        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUsername(username);
        refreshTokenEntity.setRefreshToken(refresh);
        refreshTokenEntity.setAccessToken(access);
        refreshTokenEntity.setExpiration(date.toString());

        refreshTokenRepository.save(refreshTokenEntity);
    }
}
