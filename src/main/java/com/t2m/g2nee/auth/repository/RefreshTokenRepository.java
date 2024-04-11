package com.t2m.g2nee.auth.repository;

import com.t2m.g2nee.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {


    Boolean existsByRefreshToken(String refresh);

    @Transactional
    void deleteByRefreshToken(String refresh);


}
