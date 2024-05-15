package com.t2m.g2nee.auth.service.tokenService;

import static com.t2m.g2nee.auth.filter.CustomLogoutFilter.getUsernameFromAccessToken;

import com.t2m.g2nee.auth.errorCode.ErrorResponse;
import com.t2m.g2nee.auth.repository.RefreshTokenRepository;
import com.t2m.g2nee.auth.util.AddRefreshTokenUtil;
import com.t2m.g2nee.auth.util.JWTUtil;
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
 *
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
            return makeResponse(TOKEN_IS_NULL);
        }

        //들어온 accessToken이 유효한 토큰인지 확인
        if (!jwtUtil.isValidateToken(access)) {
            return makeResponse(TOKEN_INVALID_MESSAGE);
        }

        String username = getUsernameFromAccessToken(access);

        Boolean isExist = refreshTokenRepository.existsById(username);
        if (!isExist) {
            return makeResponse(TOKEN_INVALID_MESSAGE);
        }
        String refreshToken = String.valueOf(refreshTokenRepository.findById(username).get().getRefreshToken());
        String currentAccessToken =
                String.valueOf(refreshTokenRepository.findById(username).get().getCurrentAccessToken());

        //재발급이 되어있는 accessToken인지 확인
        if (!access.equals(currentAccessToken)) {
            refreshTokenRepository.deleteById(username);
            return makeResponse(TOKEN_IS_NULL);
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            return makeResponse(TOKEN_EXPIRED_MESSAGE);
        }
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refresh")) {
            return makeResponse(TOKEN_IS_NULL);
        }

        //expired check

        response.setStatus(HttpServletResponse.SC_OK);

        Collection<? extends GrantedAuthority> authorities = jwtUtil.getAuthorities(refreshToken);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, authorities, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, authorities, 8640000L);

        //response
        addRefreshTokenUtil.addRefreshEntity(refreshTokenRepository, username, newRefresh, newAccess);
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

    private ResponseEntity<ErrorResponse> makeResponse(String message) {
        ErrorResponse response = ErrorResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}


// refresh 쿠키에 저장하지말고 redis에 저장 access, refresh 저장해서 header에 담긴 access토큰 redis DB에 있는지 검증해서 있으면
