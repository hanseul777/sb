package org.zerock.sb.security.filter;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.zerock.sb.dto.MemberDTO;
import org.zerock.sb.security.util.JWTUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
public class TokenGenerateFilter extends AbstractAuthenticationProcessingFilter {

    private JWTUtil jwtUtil;

    public TokenGenerateFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        super(defaultFilterProcessesUrl, authenticationManager);
        this.jwtUtil = jwtUtil;
    }

    //인증시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        String requestStr = extracted(request); //request 들어오는 정보를 json으로 만들어준다.

        log.info("try to login with json for api ...................");
        log.info(requestStr);

        JSONObject jObject = new JSONObject(requestStr);

        //로그인할 때 보내는 정보 => 내부적으로 로그인이 이루어진다.
        String userId = jObject.getString("userId");
        String password = jObject.getString("password");

        //제이슨 문자열을 얻어오는 코드
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userId, password);

        Authentication result = getAuthenticationManager().authenticate(authToken);

        log.info("---------------------------");
        log.info(result);

        return result;
    }

    private String extracted(HttpServletRequest request)  {
        InputStream inputStream = null;
        java.io.ByteArrayOutputStream bos = null;

        try {
            inputStream = request.getInputStream();
            bos = new ByteArrayOutputStream();
            byte[] arr = new byte[1024];

            while (true) {
                int count = inputStream.read(arr);
                if (count == -1) {
                    break;
                }
                bos.write(arr, 0, count);
            }
        }catch(Exception e){

        }finally {
            try{inputStream.close(); }catch(Exception e){}
            try {bos.close();}catch(Exception e){}
        }
        return bos.toString();
    }

    //로그인을 실패한 경우
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("unsuccessfulAuthentication: " + failed);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // json 리턴
        response.setContentType("application/json;charset=utf-8");
        JSONObject json = new JSONObject();
        String message = failed.getMessage();
        json.put("code", "401");
        json.put("message", message);

        PrintWriter out = response.getWriter();
        out.print(json);
        out.close();
    }

    //로그인을 성공한 경우 -> 사용자 정보를 가지고 토큰을 생성한다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("successfulAuthentication: " + authResult);

        MemberDTO memberDTO = (MemberDTO) authResult.getPrincipal();

        //내가 원하는 정보만 추출해서 토큰생성 후 json으로 보내주기
        String mid = memberDTO.getMid();

        log.info("MEMBER MID: " + mid);

        //토큰생성
        String token = jwtUtil.generateToken(mid);

        //보내주는 부분 : JSONObjectd에서 키, 값 바로 설정해주는 Map.of 사용
        JSONObject res = new JSONObject(Map.of("ACCESS", token));

        response.setContentType("application/json");
        PrintWriter out  = response.getWriter();
        out.println(res.toString());
        out.close();

    }
}
