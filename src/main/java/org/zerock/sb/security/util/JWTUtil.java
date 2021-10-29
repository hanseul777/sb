package org.zerock.sb.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;//암호화 라이브러리
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;

@Log4j2
public class JWTUtil {
    private final static String secretKey = "helloworld111112222233333333344444444444555555555";

    private SecretKey key;

    //공통으로 사용할거 먼저 생성
    public JWTUtil() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String content) {//content 넣으면 토큰생성할 수 있게

        long timeAmount=2; //timeAmount : 토큰의 유효 기간 (분 단위)

        String jws = Jwts.builder() // (1)
                .setSubject(content)      // (2)
                .setIssuedAt(new Date()) //발행일시
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(timeAmount).toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)           // (3) 키주기
                .compact();             // (4) 끝나면 문자열이 생성됨

        return jws;
    }

    public void validateToken(String token) throws JwtException {
        Jws<Claims> jws = Jwts.parserBuilder()  // (1)
                .setSigningKey(key)         // (2)
                .build()                    // (3)
                .parseClaimsJws(token); // (4)


    }
}
