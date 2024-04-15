package com.t2m.g2nee.auth.repository;

import com.t2m.g2nee.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {


    Boolean existsByRefreshToken(String refreshToken);

    @Transactional
    void deleteByRefreshToken(String refreshToken);


}
