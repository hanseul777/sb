package org.zerock.sb.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zerock.sb.security.filter.TokenCheckFilter;
import org.zerock.sb.security.filter.TokenGenerateFilter;
import org.zerock.sb.security.util.JWTUtil;

@Configuration
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true) // controller의 메서드에 @PreAuthorize 바로 걸 수 있게 해주는 이유
@RequiredArgsConstructor
public class CustomSecurityConfig  extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("CustomSecurityConfig..configure............");
        log.info("CustomSecurityConfig..configure............");
        log.info("CustomSecurityConfig..configure............");
        log.info("CustomSecurityConfig..configure............");

        http.formLogin().loginPage("/customLogin").loginProcessingUrl("/login"); //인가/인증에 문제시 로그인 화면
        http.csrf().disable();
        http.logout();

        //일반적인 로그인(UsernamePasswordAuthenticationFilter)이 되기전에(addFilterBefore) 필터를 작동시키는 것
        http.addFilterBefore(tokenCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        //스프링의 필터 목록에 추가 -> 어디에서 작동할건지 설정
        http.addFilterBefore(tokenGenerateFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public TokenCheckFilter tokenCheckFilter(){
        return new TokenCheckFilter(jwtUtil());
    }

    @Bean
    public TokenGenerateFilter tokenGenerateFilter()throws Exception{
        // 생성자가 두 개 들어가야한다. 하나는 문자열 : 로그인하는 경로(내부적으로 인증처리)
        //authenticationManager() : 이미 가지고 있던 인증 매니저
        // /jsonApiLogin으로 제이슨 데이터를 보냈을 때 필터가 작동하는지 확인하기 위해 필터도 스프링의 필터 목록에도 추가해야 한다.
        return new TokenGenerateFilter("/jsonApiLogin", authenticationManager(), jwtUtil() );
    }

    //토큰검증
    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil();
    }
}
