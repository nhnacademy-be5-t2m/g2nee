package com.t2m.g2nee.auth.service.tokenService;

import static com.t2m.g2nee.auth.filter.CustomLogoutFilter.getUsernameFromAccessToken;

import com.t2m.g2nee.auth.jwt.util.AddRefreshTokenUtil;
import com.t2m.g2nee.auth.jwt.util.JWTUtil;
import com.t2m.g2nee.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Collection;
import java.util.Objects;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Token Reissue Service
 * @author 김수현, 정지은
 */
@Service
public class ReissueService {


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

        //get refresh token
        String access =
                Objects.requireNonNull(request.getHeaders("Authorization").nextElement().substring("Bearer ".length()));

        //refresh null check
        if (access == null) {

            return new ResponseEntity<>(TOKEN_IS_NULL, HttpStatus.BAD_REQUEST);
        }

        String username = getUsernameFromAccessToken(access);

        Boolean isExist = refreshTokenRepository.existsById(username);
        if (!isExist) {
            return new ResponseEntity<>(TOKEN_INVALID_MESSAGE, HttpStatus.BAD_REQUEST);
        }
        String refreshToken = String.valueOf(refreshTokenRepository.findById(username).get().getRefreshToken());
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>(TOKEN_IS_NULL, HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>(TOKEN_EXPIRED_MESSAGE, HttpStatus.BAD_REQUEST);
        }
        response.setStatus(HttpServletResponse.SC_OK);

        Collection<? extends GrantedAuthority> authorities = jwtUtil.getAuthorities(refreshToken);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, authorities, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, authorities, 8640000L);

        //response
        addRefreshTokenUtil.addRefreshEntity(refreshTokenRepository, username, newRefresh);
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);

        return cookie;
    }
}


// refresh 쿠키에 저장하지말고 redis에 저장 access, refresh 저장해서 header에 담긴 access토큰 redis DB에 있는지 검증해서 있으면
