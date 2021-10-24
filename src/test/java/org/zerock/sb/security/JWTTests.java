package org.zerock.sb.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.sb.security.util.JWTUtil;

@SpringBootTest
@Log4j2
public class JWTTests {

    @Autowired
    JWTUtil jwtUtil; //완벽한 함수형태를 만들기 때문에 static으로 줘도 상관없지만 연결할게 많아서 이런 형식으로 만든다.

    @Test
    public void testGenerate(){

        String jwtStr = jwtUtil.generateToken("user11");

        log.info(jwtStr);
    }

    @Test
    public void testValidate(){

        // 생성했던 문자열 토큰 : 사이트에서는 유효한걸 확인했고, 메서드에서도 유효한지 확인을 진행(만료된 토큰인지 확인)
        String str = "eyJhGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTEiLCJpYXQiOjE2MzUwMDYwMjEsImV4cCI6MTYzNTAwOTYyMX0.X2izxZI5sEGV0vkrzsAThFUuI9FqJ7MWvHix2jPAaz4";

        try{
            jwtUtil.validateToken(str);
        }catch (ExpiredJwtException ex){
            log.error("expired...........");

            log.error(ex.getMessage());

        }catch (JwtException ex){
            log.info("jwtException........");
            log.error(ex.getMessage());
        }

    }
}
