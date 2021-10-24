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
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("-----------------------------");
        log.info("------loadUserByUsername------" + username);//username제대로 들어오는지 확인

        Optional<Member> optionalMember = memberRepository.getMemberEager(username);

        Member member = optionalMember.orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND"));

        log.info("Member: " + member);

        MemberDTO memberDTO = MemberDTO.builder()
                .mid(member.getMid())
                .mpw(member.getMpw())
                .mname(member.getMname())
                .roles(member.getRoleSet().stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_"+memberRole.name())).collect(Collectors.toSet()))
                .build();

        log.info(memberDTO);

        log.info("-------------------------------------------------");
        log.info("-------------------------------------------------");

        return memberDTO;
    }

    private final MemberRepository memberRepository;
}
