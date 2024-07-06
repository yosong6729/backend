package backend.time.handler;

import backend.time.config.jwt.JwtTokenUtil;
import backend.time.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginFailureHandler implements AuthenticationFailureHandler {
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisTemplate<String, String> redisTemplate;
    @Override
    @Transactional
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)throws IOException, ServletException {
        System.out.println("login fail, login error message : "+exception.fillInStackTrace());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(new ObjectMapper().writeValueAsString(
                new ResponseDto<>(response.getStatus(), "not our member or strange token")));
    }
}
