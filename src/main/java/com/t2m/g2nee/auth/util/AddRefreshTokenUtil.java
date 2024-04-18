package com.t2m.g2nee.auth.util;

import com.t2m.g2nee.auth.entity.RefreshToken;
import com.t2m.g2nee.auth.repository.RefreshTokenRepository;
import javax.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class AddRefreshTokenUtil {

    private final RefreshTokenRepository refreshTokenRepository;

    public AddRefreshTokenUtil(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void addRefreshEntity(RefreshTokenRepository refreshTokenRepository, String username, String refresh) {


        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUsername(username);
        refreshTokenEntity.setRefreshToken(refresh);

        refreshTokenRepository.save(refreshTokenEntity);
    }
}
