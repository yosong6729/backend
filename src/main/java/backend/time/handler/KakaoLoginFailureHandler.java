package backend.time.handler;

import backend.time.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    @Transactional
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)throws IOException, ServletException {
        System.out.println("로그인 실패, 에러 메세지 : "+exception.getMessage());

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("회원이 아니거나 토큰이 잘못되었습니다.");
    }
}
