package com.t2m.g2nee.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t2m.g2nee.auth.filter.CustomLoginAuthenticationFilter;
import com.t2m.g2nee.auth.filter.JWTFilter;
import com.t2m.g2nee.auth.jwt.util.AddRefreshTokenUtil;
import com.t2m.g2nee.auth.jwt.util.JWTUtil;
import com.t2m.g2nee.auth.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {



    private final AuthenticationConfiguration authenticationConfiguration;

    private final JWTUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;
    private final AddRefreshTokenUtil addRefreshTokenUtil; // 레디스에 토큰 저장

    private final ObjectMapper objectMapper;
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,JWTUtil jwtUtil,RefreshTokenRepository refreshTokenRepository,AddRefreshTokenUtil addRefreshTokenUtil,ObjectMapper objectMapper) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil =jwtUtil;
        this.refreshTokenRepository=refreshTokenRepository;
        this.addRefreshTokenUtil = addRefreshTokenUtil;
        this.objectMapper=objectMapper;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors((cors)-> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();
                                configuration.setAllowedOrigins(Collections.singletonList("http://133.186.208.183:8100/"));
                                configuration.setAllowedOrigins(Collections.singletonList("http://g2nee:g2nee@133.186.150.129:8761/eureka"));
                                configuration.setAllowedMethods(Collections.singletonList("*"));
                                configuration.setAllowCredentials(true);
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L);

                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                                return configuration;
                            }
                        }));

        http
                .csrf((auth) -> auth.disable());
        http
                .formLogin((auth)-> auth.disable());

        http
                .httpBasic((auth)-> auth.disable());

//        http
//                .authorizeHttpRequests((auth)-> auth
//                        .antMatchers("/auth/login").permitAll()
//                        .antMatchers("/auth/reissue").permitAll()
//                        .anyRequest().authenticated());




        http
                .addFilterBefore(new JWTFilter(jwtUtil), CustomLoginAuthenticationFilter.class);

        http
                .addFilterAt(new CustomLoginAuthenticationFilter(authenticationManager(authenticationConfiguration),jwtUtil,refreshTokenRepository, addRefreshTokenUtil,objectMapper), UsernamePasswordAuthenticationFilter.class);



        http
                .sessionManagement((session)-> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }


}


