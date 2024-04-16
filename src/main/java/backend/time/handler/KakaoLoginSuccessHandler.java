package backend.time.handler;

import backend.time.config.auth.PrincipalDetail;
import backend.time.config.jwt.JwtTokenUtil;
import backend.time.dto.ResponseDto;
import backend.time.model.Member_Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//@Slf4j
@RequiredArgsConstructor
@RestController
@Component(value = "authenticationSuccessHandler")
public class KakaoLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private final JwtTokenUtil jwtTokenUtil;
    @Autowired
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //여기서 회원가입으로 빠지게 할거임

        PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();
        System.out.println("사용자" + principalDetail.getUsername() + " 가 로그인했습니다.");

        if (principalDetail.getMember().getRole() == Member_Role.USER) { // 회원일 경우
            //access 토큰 생성
            String accessToken = jwtTokenUtil.generateToken((PrincipalDetail) authentication.getPrincipal());
            //refresh 토큰 생성
            String refreshToken = jwtTokenUtil.generateRefreshToken((PrincipalDetail) authentication.getPrincipal());

            // Redis에 Refresh Token 저장

            String nickname = ((UserDetails) authentication.getPrincipal()).getUsername();
            redisTemplate.opsForValue().set("refresh token:" + nickname, refreshToken);
            redisTemplate.expire("refresh token:" + nickname, jwtTokenUtil.getRefreshExpirationTime(), TimeUnit.MILLISECONDS);

            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json;charset=UTF-8");

            Map<String, Object> data = new HashMap<>();
            data.put("isOurMemeber", true);
            data.put("accessToken", accessToken);
            data.put("refreshToken", refreshToken);

            response.getWriter().println(new ObjectMapper().writeValueAsString(
                    new ResponseDto<>(response.getStatus(), data)
            ));

        } else {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json;charset=UTF-8");

            Map<String, Object> data = new HashMap<>();
            data.put("isOurMemeber", false);
            data.put("accessToken", null);
            data.put("refreshToken", null);

            response.getWriter().println(new ObjectMapper().writeValueAsString(
                    new ResponseDto<>(response.getStatus(), data)
            ));
        }
    }
}



/*        //access 토큰 생성
        String accessToken = jwtTokenUtil.generateToken((PrincipalDetail)authentication.getPrincipal());
        //refresh 토큰 생성
        String refreshToken = jwtTokenUtil.generateRefreshToken((PrincipalDetail)authentication.getPrincipal());
        // Redis에 Refresh Token 저장
        String nickname = ((UserDetails) authentication.getPrincipal()).getUsername();
        redisTemplate.opsForValue().set("refresh token:"+ nickname, refreshToken);
        redisTemplate.expire("refresh token:"+nickname,jwtTokenUtil.getRefreshExpirationTime(), TimeUnit.MILLISECONDS);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> data = new HashMap<>();
        data.put("message", "로그인 성공");
        data.put("token", accessToken);
        data.put("refreshToken", refreshToken);

        response.getWriter().println(new ObjectMapper().writeValueAsString(
                new ResponseDto<>(response.getStatus(), data)));*/







