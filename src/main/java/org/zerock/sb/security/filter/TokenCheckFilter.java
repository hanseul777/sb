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
//외부에서 ajax로 접근할 때 토큰이있는지 확인
public class TokenCheckFilter extends OncePerRequestFilter {

    private JWTUtil jwtUtil;

    //토큰을 검증
    public TokenCheckFilter(JWTUtil jwtUtil) {
        this.jwtUtil=jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("-------TokenCheckFilter-------");

        String path=request.getRequestURI();//어떤 경로를 호출하려는지 알아내는 것
        log.info(path);

        if(path.startsWith("/api/")){
            // /api/ 시작하는 경로로 들어 오면 토큰을 확인해라: 그 정보 중에 Authorization 값을 끄집어 냄
            //Authorization 정보가 없으면 막아야 함
            //check token : request로 들어온 정보중에 header에 authorization이 있는지 확인
            String authToken=request.getHeader("Authorization");
            if(authToken==null) { // 원래는 없는경우와 검증을 실패한 경우를 생각해야한다 -> 이건 없을 때
                log.info("authToken is null........................."); //토큰없으면 메시지 발생

                //토큰 없으니까 아무 것도 못 한다고 막는 것
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
            //jwt 검사: 맨 앞에 인증 타입이 있는데 우리는 Bearer 사용함, Bearer 뒤가 토큰값이니까 Bearer를 잘라내야 함
            String tokenStr = authToken.substring(7); //Bearer 6자리 + 뒤에 공백 1

            try {
                jwtUtil.validateToken(tokenStr);
            } catch (ExpiredJwtException ex) {//만료된 토큰일 때

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
            } catch (JwtException jex) {//잘못된 토큰일 때

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

            filterChain.doFilter(request, response);//토큰 정보 맞으면 이후 단계 진행

        }else { // api로 시작하지 않으면 다음단계로 넘어감
            log.info("======================TokenCheckFilter============================");
            //다음단계로 갈 수 있는 기능 : 문제가 생기면 메시지로 튕겨야한다(JSON Object를 사용할 것)
            //정상 작동하니까 다음 단계로 진행
            filterChain.doFilter(request, response);
        }

    }
}
