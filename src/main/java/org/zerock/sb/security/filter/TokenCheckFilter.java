package org.zerock.sb.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.sb.security.util.JWTUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class TokenCheckFilter extends OncePerRequestFilter {

    private JWTUtil jwtUtil;

    //토큰을 검증
    public TokenCheckFilter(JWTUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("--------------TokenCheckFilter--------------------");

        //어느 경로에서 호출하는지
        String path = request.getRequestURI();

        log.info(path);

        if(path.startsWith("/api/")){//URI가 api로 시작하면 토큰을 검증
            //check token : request로 들어온 정보중에 header에 authorization이 있는지 확인
            String authToken  = request.getHeader("Authorization");

            if(authToken == null){ // 원래는 없는경우와 검증을 실패한 경우를 생각해야한다 -> 이건 없을 때
                log.info("authToken is null........................."); //토큰없으면 메시지 발생

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                // json 리턴
                response.setContentType("application/json;charset=utf-8");
                JSONObject json = new JSONObject();
                String message = "FAIL CHECK API TOKEN";
                json.put("code", "403");
                json.put("message", message);

                PrintWriter out = response.getWriter();
                out.print(json);
                out.close();
                return;//검증에 문제가 있으면 여기에서 return (끝)
            }

            //jwt(authToken) 검사 : exception으로 처리해줘야함
            //jwt검사 맨앞에 인증 타입 Bearer 토큰
            String tokenStr = authToken.substring(7); //Bearer 6자리 + 뒤에 공백 1

            try {
                jwtUtil.validateToken(tokenStr);
            }catch(ExpiredJwtException ex){//일반에러 : 너 토큰 좀 이상해

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // json 리턴
                response.setContentType("application/json;charset=utf-8");
                JSONObject json = new JSONObject();
                String message = "EXPIRED API TOKEN.. TOO OLD";
                json.put("code", "401");
                json.put("message", message);

                PrintWriter out = response.getWriter();
                out.print(json);
                out.close();
                return;
            }catch(JwtException jex){//인증만료 된 경우 : 토큰 너무 오래된거야

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // json 리턴
                response.setContentType("application/json;charset=utf-8");
                JSONObject json = new JSONObject();
                String message = "YOUR ACCESS TOKEN IS INVALID";
                json.put("code", "401");
                json.put("message", message);

                PrintWriter out = response.getWriter();
                out.print(json);
                out.close();
                return;
            }

            filterChain.doFilter(request, response); //검증에 문제가 없으면 가던데로 다음단계로 간다.

        }else { // api로 시작하지 않으면 다음단계로 넘어감
            log.info("======================TokenCheckFilter============================");
            //다음단계로 갈 수 있는 기능 : 문제가 생기면 메시지로 튕겨야한다(JSON Object를 사용할 것)
            filterChain.doFilter(request, response);
        }

    }
}
