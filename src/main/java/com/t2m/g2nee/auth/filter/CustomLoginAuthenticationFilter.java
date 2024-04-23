package com.t2m.g2nee.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t2m.g2nee.auth.dto.member.MemberLoginDTO;
import com.t2m.g2nee.auth.exception.token.MemberDTOParsingException;
import com.t2m.g2nee.auth.repository.RefreshTokenRepository;
import com.t2m.g2nee.auth.util.AddRefreshTokenUtil;
import com.t2m.g2nee.auth.util.JWTUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//

/**
 * 정상로그인시 jwt발급 필터
 * (username, password를 front로부터 받아 shopDB검증후 일치하면)
 **/
public class CustomLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    private AddRefreshTokenUtil addRefreshTokenUtil;

    private final ObjectMapper objectMapper;

    private final RedisTemplate<String, String> redisTemplate;

    public CustomLoginAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                                           RefreshTokenRepository refreshTokenRepository,
                                           AddRefreshTokenUtil addRefreshTokenUtil, ObjectMapper objectMapper,
                                           RedisTemplate<String, String> redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.addRefreshTokenUtil = addRefreshTokenUtil;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
        super.setAuthenticationManager(authenticationManager);
        super.setFilterProcessesUrl("/api/v1/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest,
                                                HttpServletResponse httpServletResponse)
            throws AuthenticationException {


        /**
         * 요청에 담긴 username, password담기
         */

        MemberLoginDTO memberLoginDTO;

        try {
            memberLoginDTO = objectMapper.readValue(httpServletRequest.getInputStream(), MemberLoginDTO.class);


        } catch (IOException e) {
            throw new MemberDTOParsingException();
        }

        /**
         * shopDB에 회원정보가 있는지 확인 -> service로 구현, 불러올수 있게
         * DTO같은 역할 토큰에 담아 인증 로직을 처리할 Manager로 전달
         */
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberLoginDTO.getUsername(), memberLoginDTO.getPassword());
        return authenticationManager.authenticate(authenticationToken);
    }


    /**
     * 로그인 성공시 실행하는 메서드
     * 로그인 성공시 로그인한 객체 가져와서  username, role 값넣어서 token 만들어줌
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest httpServletRequest,
                                            HttpServletResponse httpServletResponse, FilterChain filterChain,
                                            Authentication authentication) {
        //로그인 완료시 successAuthentication 메서드 또는 SuccessHandler에서 Access/Refresh

        String username = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String access = jwtUtil.createJwt("access", username, authentication.getAuthorities(), 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, authentication.getAuthorities(), 86400000L);

        addRefreshTokenUtil.addRefreshEntity(refreshTokenRepository, username, refresh);
        httpServletResponse.setHeader("access", access);
        httpServletResponse.addCookie(createCookie("refresh", refresh));
        httpServletResponse.setStatus(HttpStatus.OK.value());


    }


    /**
     * 로그인 실패시 실행하는 메서드
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest httpServletRequest,
                                              HttpServletResponse httpServletResponse, AuthenticationException failed) {
        httpServletResponse.setStatus(401);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
