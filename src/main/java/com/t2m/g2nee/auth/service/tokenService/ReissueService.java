package com.t2m.g2nee.auth.service.tokenService;

import com.t2m.g2nee.auth.jwt.util.AddRefreshTokenUtil;
import com.t2m.g2nee.auth.jwt.util.JWTUtil;
import com.t2m.g2nee.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

@Service
public class ReissueService {

    private static final String EXP_MESSAGE = "다시 로그인 하세요.";
    private static final String TOKEN_EXPIRED_MESSAGE = "토큰이 만료되었습니다.";

    private static final String TOKEN_INVALID_MESSAGE = "유효하지않은 토큰입니다";

    private static final String TOKEN_IS_NULL = "토큰이 존재하지 않습니다.";
    //private static final String BLACK_LIST = "black_list";
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    private final AddRefreshTokenUtil addRefreshTokenUtil;


    public ReissueService(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository,
                          AddRefreshTokenUtil addRefreshTokenUtil) {
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
        //!!!!!!!db에서 꺼내오는걸로

        if (refresh == null) {

            return new ResponseEntity<>(TOKEN_IS_NULL, HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>(TOKEN_EXPIRED_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            return new ResponseEntity<>(TOKEN_INVALID_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        Boolean isExist = refreshTokenRepository.existsById(refresh);
        if (!isExist) {
            return new ResponseEntity<>(TOKEN_IS_NULL, HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        Collection<? extends GrantedAuthority> authorities = jwtUtil.getAuthorities(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, authorities, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, authorities, 8640000L);
        //response

        refreshTokenRepository.deleteById(refresh);
        addRefreshTokenUtil.addRefreshEntity(refreshTokenRepository, username, newRefresh, newAccess, 86400000L);
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


// refresh 쿠키에 저장하지말고 redis에 저장 access, refresh 저장해서 header에 담긴 access토큰 redis DB에 있는지 검증해서 있으면
