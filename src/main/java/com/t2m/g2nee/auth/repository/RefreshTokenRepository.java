package com.t2m.g2nee.auth.repository;

import com.t2m.g2nee.auth.entity.RefreshToken;
import javax.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;


public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {


    boolean existsById(String refreshToken);

    @Transactional
    void deleteById(String refreshToken);


}
