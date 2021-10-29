package org.zerock.sb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.sb.dto.MemberDTO;
import org.zerock.sb.entity.Member;
import org.zerock.sb.repository.MemberRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor //MemberRepository 거쳐서 나와야 하기 때문에 !
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("-----------------------------");
        log.info("-------loadUserByUsername---------" + username);

        Optional<Member> optionalMember = memberRepository.getMemberEager(username); //username 찾기

        Member member = optionalMember.orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND")); //username 없을 때 에러처리

        log.info("Member: " + member);

        log.info("---------------------------------------------------");

        MemberDTO memberDTO = MemberDTO.builder()
                .mid(member.getMid())
                .mpw(member.getMpw())
                .mname(member.getMname())
                .roles(member.getRoleSet().stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_"+memberRole.name())).collect(Collectors.toSet())) //SimpleGrantedAuthority 권한 주기
                .build();

        log.info(memberDTO);

        log.info("-------------------------------------------------");
        log.info("-------------------------------------------------");

        return memberDTO;

    }

}
