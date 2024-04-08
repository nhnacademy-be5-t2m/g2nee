package com.t2m.g2nee.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    //데이터베이스에서 특정 유저 조회해 리턴->DB연결

    private final MemberRepository memberrRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member userData = memberRepository.findByUsername(username);

        if(userData != null){
            return new CustomUserDetails(userData);
        }
        return null;
    }
}
