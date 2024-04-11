package com.t2m.g2nee.auth.filter;


import com.t2m.g2nee.auth.jwt.util.JWTUtil;
import com.t2m.g2nee.auth.service.memberService.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;


public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    private final CustomUserDetailsService customUserDetailsService;


    public JWTFilter(JWTUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {

        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * DoFilter를 통한 토큰 검증
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("access");

        //토큰 없을 시 다음 필터로
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            jwtUtil.isExpired(accessToken);

        } catch (ExpiredJwtException e) {
            PrintWriter writer = response.getWriter();
            writer.print("accessToken이 만료되었습니다.");

            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            PrintWriter writer = response.getWriter();
            writer.print("accessToken이 유효하지않습니다.");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        String username = jwtUtil.getUsername(accessToken);
        Collection<? extends GrantedAuthority> authorities = jwtUtil.getAuthorities(accessToken);

        // UserDetailsService를 사용하여 사용자 정보 가져오기
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Authentication 객체 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
//Access 토큰 요청을 검증하는 JWTFilter에서 Access토큰이 만료된 경우 프론트 개발자와 협의된 상태 코드와 메세지 응답
//프론트측 API클라이언트 요청시 Access토큰 만료 요청이 오면 예외문을 통해 refresh토큰을 서버측으로 전송하고 Access토큰을 발급받는 로직
//서버 측에서는 refresh 토큰을 받을 엔드포인트(컨트롤러)를 구성해 refresh를 검증하고 Access를 응답