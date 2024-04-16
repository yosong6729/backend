package backend.time.controller;

import backend.time.config.auth.PrincipalDetail;
import backend.time.config.auth.PrincipalDetailService;
import backend.time.config.jwt.JwtTokenUtil;
import backend.time.dto.*;
import backend.time.model.Member;
import backend.time.model.Member_Role;
import backend.time.repository.MemberRepository;
import backend.time.service.MemberService;
import ch.qos.logback.core.subst.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@RestController
@PropertySource(value={"application-mysql.properties"})
public class MemberApiController {
    private final MemberService memberService;

    @Autowired
    private PrincipalDetailService principalDetailService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final StringRedisTemplate redisTemplate;

 // 프론트 없이 토큰 받아올 때 쓴 거
/*   @GetMapping("/oauth/kakao")
    public ResponseDto ex1(@RequestParam(value = "code") String code){
        System.out.println("token "+code);
        String token = memberService.getReturnAccessToken(code);

        Map<String, Object> data = new HashMap<>();
        data.put("token",token);
        return new ResponseDto(HttpStatus.OK.value(), data);
    }*/

    //카카오에서 사용자 정보 갖고오기

    @PostMapping("kakao/getinfo")
    public ResponseDto getInfo(@RequestBody TokenDto token) {
        Map<String, Object> data = new HashMap<>();
        System.out.println("token " + token.getToken());
        Member member = memberService.getUserInfo(token.getToken());
        if(member == null){
            return new ResponseDto(HttpStatus.FORBIDDEN.value(), "잘못된 토큰입니다.");
        }
        else{
            data.put("kakaoId",member.getKakaoId());
            return new ResponseDto(HttpStatus.OK.value(), data);
        }
    }


/*    @PostMapping("/kakao/login") //프론트가 결과가 true면 jwt요청해야하고, false면 회원가입으로 유도
    public ResponseDto login(@RequestBody TokenDto tokenDto){
        Map<String, Object> data = new HashMap<>();
        System.out.println("token "+tokenDto.getToken());
        Member member = memberService.getUserInfo(tokenDto.getToken());
        System.out.println("로그인한 멤버 role "+member.getRole());
        if(member.getRole() == Member_Role.GUEST){ //회원 x
            data.put("isOurMember",false);
            data.put("kakaoId",member.getKakaoId());

            return new ResponseDto(HttpStatus.FORBIDDEN.value(), data);
        }
        else{
            data.put("isOurMember",true);
            data.put("kakaoId",member.getKakaoId());
            return new ResponseDto(HttpStatus.OK.value(), data);
        }

    }*/

 /*   @PostMapping("/login/jwt")
    public ResponseDto loginjwt(@RequestBody KakaoDto kakaoDto){
        Map<String, Object> data = new HashMap<>();

        PrincipalDetail principalDetail = (PrincipalDetail) principalDetailService.loadUserByUsername(kakaoDto.getKakaoId());
        if(principalDetail == null){
            data.put("isOurMemeber", false);
            data.put("accessToken",null);
            data.put("refreshToken",null);
        }
        else{
            String accessToken = jwtTokenUtil.generateToken(principalDetail);
            String refreshToken = jwtTokenUtil.generateRefreshToken(principalDetail);

            String kakaoId = (principalDetail.getUsername());
            redisTemplate.opsForValue().set("refresh token:" + kakaoId, refreshToken);
            redisTemplate.expire("refresh token:" + kakaoId, jwtTokenUtil.getRefreshExpirationTime(), TimeUnit.MILLISECONDS);

            data.put("isOurMemeber", true);
            data.put("accessToken",accessToken);
            data.put("refreshToken",refreshToken);
        }



        return new ResponseDto(HttpStatus.OK.value(), data);
    }*/
/*    @PostMapping("/login/jwt")
    public ResponseDto loginjwt(@RequestBody KakaoDto kakaoDto){
        Map<String, Object> data = new HashMap<>();

        PrincipalDetail principalDetail = (PrincipalDetail) principalDetailService.loadUserByUsername(kakaoDto.getKakaoId());
        System.out.println("username"+ principalDetail.getUsername());
        System.out.println("password"+ principalDetail.getPassword());
        data.put("accessToken",jwtTokenUtil.generateToken(principalDetail));
        data.put("refresh",jwtTokenUtil.generateRefreshToken(principalDetail));

        return new ResponseDto(HttpStatus.OK.value(), data);
    }*/

    @PutMapping("/nickname/change")
    public ResponseDto changeName(@AuthenticationPrincipal PrincipalDetail principalDetail,@RequestBody @Valid NicknameDto nicknameDto) {
        Boolean isChange = memberService.changeNickname(principalDetail.getMember(), nicknameDto.getNickname());
        Map<String,Object> data = new HashMap<>();
        data.put("isChange",isChange);
        if(isChange){
            return new ResponseDto(HttpStatus.OK.value(), data);
        }
        else{
            return new ResponseDto(HttpStatus.FORBIDDEN.value(), data);
        }
    }

    @DeleteMapping("/delete/member")
    public ResponseDto deleteMember(@AuthenticationPrincipal PrincipalDetail principalDetail){
        memberService.deleteMember(principalDetail.getMember());
        Map<String,Object> data = new HashMap<>();
        data.put("isDelete",true);

        return new ResponseDto(HttpStatus.OK.value(), data);
    }


    // 회원 가입 완료 버튼 눌렀을 때 (위치 미포함)
    @PutMapping("/sign-up")
    public ResponseDto saveMember(@RequestBody @Valid UnfinishedMemberDto unfinishedMemberDto) throws Exception{
//        System.out.println("Id "+unfinishedMemberDto.getKakaoId());
        Map<String, Boolean> data = new HashMap<>();

        boolean isSuccess = memberService.saveMember(unfinishedMemberDto.getKakaoId(), unfinishedMemberDto.getNickname());
        if(isSuccess){
            data.put("success", true);
            return new ResponseDto(HttpStatus.OK.value(), data);
        }
        else{
            data.put("success", false);
            return new ResponseDto(HttpStatus.FORBIDDEN.value(), data);
        }

    }

    // 중복 검사 버튼 눌렀을 때
    @PostMapping("/sign-up/nicknameCheck")
    public ResponseDto nicknameDuplicated(@RequestBody @Valid NicknameDto nicknameDto){
        Map<String, Boolean> data = new HashMap<>();
        System.out.println("nickname"+nicknameDto.getNickname());
        if(memberService.isNicknameDuplicated(nicknameDto.getNickname())){ //중복됨
            data.put("success",false);
            return new ResponseDto(HttpStatus.FORBIDDEN.value(), data);
        }
        else{
            data.put("success",true);
            return new ResponseDto(HttpStatus.OK.value(), data);

        }
    }

    //받은 refresh token을 redis에 있는 refresh token과 비교
    //맞다면 유효성 검사
    //유효하다면 accessToken 발급 및 refresh token 재발급 , 기존 refresh Token 무효화 시키기
    @PostMapping("/token/refresh")
    public ResponseDto refresh(@RequestBody TokenDto tokenDto){
        Map<String, Object> data = new HashMap<>();

        if(jwtTokenUtil.validateToken(tokenDto.getToken())) {
            String kakaoId = jwtTokenUtil.extractUsername(tokenDto.getToken());
            if (kakaoId != null && tokenDto.getToken().equals(redisTemplate.opsForValue().get("refresh token:" + kakaoId))) {
                PrincipalDetail principalDetail = (PrincipalDetail) principalDetailService.loadUserByUsername(kakaoId);
                String accessToken = jwtTokenUtil.generateToken(principalDetail);
                String refreshToken = jwtTokenUtil.generateRefreshToken(principalDetail);

                redisTemplate.opsForValue().set("refresh token:" + kakaoId, refreshToken);
                redisTemplate.expire("refresh token:" + kakaoId, jwtTokenUtil.getRefreshExpirationTime(), TimeUnit.MILLISECONDS);

                data.put("isOurMemeber", true);
                data.put("accessToken", accessToken);
                data.put("refreshToken", refreshToken);
                return new ResponseDto(HttpStatus.OK.value(), data);
            }
            else{
                data.put("isOurMemeber", false);
                data.put("accessToken",null);
                data.put("refreshToken",null);
                return new ResponseDto(HttpStatus.FORBIDDEN.value(),data);
            }
        }

        else{
            data.put("tokenIsExpired", true);
            return new ResponseDto(HttpStatus.FORBIDDEN.value(),data);
        }

    }


    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

}
