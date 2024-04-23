package com.t2m.g2nee.auth.filter;

import com.t2m.g2nee.auth.repository.RefreshTokenRepository;
import com.t2m.g2nee.auth.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.web.filter.GenericFilterBean;

public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {

        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/api\\/v1\\/auth\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        //get refresh token
        String access =
                Objects.requireNonNull(request.getHeaders("Authorization").nextElement().substring("Bearer ".length()));

        //refresh null check
        if (access == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String username = getUsernameFromAccessToken(access);

        Boolean isExist = refreshTokenRepository.existsById(username);
        if (!isExist) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String refreshToken = String.valueOf(refreshTokenRepository.findById(username).get().getRefreshToken());
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refresh")) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //expired check
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        refreshTokenRepository.deleteById(username);

        response.setStatus(HttpServletResponse.SC_OK);

    }

    public static String getUsernameFromAccessToken(String accessToken) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] access_chunks = accessToken.split("\\.");
        String access_payload = new String(decoder.decode(access_chunks[1]));
        JSONObject aObject = new JSONObject(access_payload);
        String username = aObject.getString("username");
        return username;
    }
}

