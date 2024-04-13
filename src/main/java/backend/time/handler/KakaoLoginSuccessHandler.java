package backend.time.handler;

import backend.time.config.auth.PrincipalDetail;
import backend.time.config.jwt.JwtTokenUtil;
import backend.time.dto.ResponseDto;
import backend.time.model.CustomOAuth2User;
import backend.time.model.KakaoOauth2UserInfo;
import backend.time.model.Member;
import backend.time.model.Member_Role;
import backend.time.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final MemberRepository memberRepository;

    private final JwtTokenUtil jwtTokenUtil;
    private final RedisTemplate<String, String> redisTemplate;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("로그인 성공");
        System.out.println(authentication.getPrincipal());
        //access 토큰 생성
        String accessToken = jwtTokenUtil.generateToken((PrincipalDetail) authentication.getPrincipal());
        System.out.println("토큰 생성 에러");

        //refresh 토큰 생성
        String refreshToken = jwtTokenUtil.generateRefreshToken((PrincipalDetail) authentication.getPrincipal());

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
                new ResponseDto<>(response.getStatus(), data)));

    }

}
/*
        CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();
        System.out.println("principal에서 꺼낸 user"+oAuth2User);


        if(oAuth2User.getRole()== Member_Role.GUEST) { // 회원이 아닐 경우
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> data = new HashMap<>();
        data.put("isOurMemeber", false);


        response.getWriter().println(new ObjectMapper().writeValueAsString(
        new ResponseDto<>(response.getStatus(), data)
        ));
        }

        else{ // 회원이면
        //access 토큰 생성
        String accessToken = jwtTokenUtil.generateToken((PrincipalDetail) authentication.getPrincipal());
        //refresh 토큰 생성
        String refreshToken = jwtTokenUtil.generateRefreshToken((PrincipalDetail) authentication.getPrincipal());

        // Redis에 Refresh Token 저장

        String nickname = ((UserDetails) authentication.getPrincipal()).getUsername();
        redisTemplate.opsForValue().set("refresh token:"+ nickname, refreshToken);
        redisTemplate.expire("refresh token:"+nickname,jwtTokenUtil.getRefreshExpirationTime(), TimeUnit.MILLISECONDS);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> data = new HashMap<>();
        data.put("isOurMemeber", true);
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);

        response.getWriter().println(new ObjectMapper().writeValueAsString(
        new ResponseDto<>(response.getStatus(), data)
        ));      }*/
