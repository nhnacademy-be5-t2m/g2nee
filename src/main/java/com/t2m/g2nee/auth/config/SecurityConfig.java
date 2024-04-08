package com.t2m.g2nee.auth.config;

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

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,JWTUtil jwtUtil,RefreshTokenRepository refreshTokenRepository,AddRefreshTokenUtil addRefreshTokenUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil =jwtUtil;
        this.refreshTokenRepository=refreshTokenRepository;
        this.addRefreshTokenUtil = addRefreshTokenUtil;
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
                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
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

        http
                .authorizeHttpRequests((auth)-> auth
                        .antMatchers("/login","/","/join").permitAll()
                        .antMatchers("/auth/reissue").permitAll()
                        .antMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());




        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtUtil,refreshRepository, addRefreshTokenUtil), UsernamePasswordAuthenticationFilter.class);



        http
                .sessionManagement((session)-> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }


}


