package com.t2m.g2nee.auth.service.memberService;

import com.t2m.g2nee.auth.adaptor.MemberAdaptor;
import com.t2m.g2nee.auth.dto.member.MemberInfoRequestDTO;
import com.t2m.g2nee.auth.dto.member.MemberInfoResponseDTO;
import com.t2m.g2nee.auth.exception.member.NotMemberOfG2nee;
import java.util.List;
import java.util.stream.Collectors;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {


    private final MemberAdaptor memberAdaptor;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        ResponseEntity<MemberInfoResponseDTO> memberData;


        try {
            memberData = memberAdaptor.loginRequest(new MemberInfoRequestDTO(username));

        } catch (HttpClientErrorException e) {
            throw new NotMemberOfG2nee();
        }
        MemberInfoResponseDTO memberInfoResponseDTO = memberData.getBody();

        List<SimpleGrantedAuthority> grantedAuthorities =
                memberInfoResponseDTO.getAuthorities().stream().map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());


        return new User(memberInfoResponseDTO.getUsername().toString(), memberInfoResponseDTO.getPassword().toString(),
                grantedAuthorities);

    }
}
