package com.t2m.g2nee.auth.service;

import com.t2m.g2nee.auth.jwt.util.AddRefreshTokenUtil;
import com.t2m.g2nee.auth.jwt.util.JWTUtil;
import com.t2m.g2nee.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class ReissueService {
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    private final AddRefreshTokenUtil addRefreshTokenUtil;


    public ReissueService(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, AddRefreshTokenUtil addRefreshTokenUtil){
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.addRefreshTokenUtil = addRefreshTokenUtil;

    }

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        Boolean isExist = refreshTokenRepository.existsByRefreshToken(refresh);
        if(!isExist){
            return new ResponseEntity<>("invalid refresh token",HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh",username,role,8640000L);
        //response

        refreshTokenRepository.deleteByRefreshToken(refresh);
        addRefreshTokenUtil.addRefreshEntity(refreshTokenRepository,username,newRefresh,86400000L);
        response.setHeader("access", newAccess);
        response.addCookie(createCookie ("refresh",newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }
    private Cookie createCookie(String key, String value){
        Cookie cookie = new Cookie(key,value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);

        return cookie;
    }
}



