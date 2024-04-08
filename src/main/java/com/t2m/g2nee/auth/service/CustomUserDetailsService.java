package com.t2m.g2nee.auth.service;

import com.t2m.g2nee.auth.Adaptor.MemberAdaptor;
import com.t2m.g2nee.auth.dto.member.MemberInfoRequestDTO;
import com.t2m.g2nee.auth.dto.member.MemberInfoResponseDTO;
import com.t2m.g2nee.auth.exception.member.NotMemberOfG2nee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    //데이터베이스에서 특정 유저 조회해 리턴->DB연결

    private final MemberAdaptor memberAdaptor;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Member userData = memberRepository.findByUsername(username);
        ResponseEntity<MemberInfoResponseDTO> memberData;


        try {
            memberData = memberAdaptor.loginRequest(new MemberInfoRequestDTO(username));

        }catch (HttpClientErrorException e){
            throw new NotMemberOfG2nee();
        }
        MemberInfoResponseDTO memberInfoResponseDTO = memberData.getBody();

        List<SimpleGrantedAuthority> grantedAuthorities = memberInfoResponseDTO.getAuthorities().stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new User(memberInfoResponseDTO.getUsername().toString(),memberInfoResponseDTO.getPassword().toString(),grantedAuthorities);
    }
}
// 원래 한 프로젝트에 있는거 shop에 있는 DB로 전달해 처리해야하므로 , MemberAdaptor설정
//Member를 MemberResponseDto로 받아서 처리