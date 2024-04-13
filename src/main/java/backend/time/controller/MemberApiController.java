package backend.time.controller;

import backend.time.config.auth.PrincipalDetail;
import backend.time.config.auth.PrincipalDetailService;
import backend.time.config.jwt.JwtTokenUtil;
import backend.time.dto.*;
import backend.time.model.Member;
import backend.time.model.Member_Role;
import backend.time.service.MemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


@RequiredArgsConstructor
@RestController
@PropertySource(value={"application-mysql.properties"})
public class MemberApiController {
    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PrincipalDetailService principalDetailService;

    @GetMapping("/oauth/kakao")
    public ResponseDto ex1(@RequestParam(value = "code") String code){
        System.out.println("token "+code);
        String token = memberService.getReturnAccessToken(code);

        Map<String, Object> data = new HashMap<>();
        return new ResponseDto(HttpStatus.OK.value(), data);
    }
    @PostMapping("/kakao/login") //프론트가 결과가 true면 jwt요청해야하고, false면 회원가입으로 유도
    public ResponseDto ex(@RequestBody TokenDto token){
        Map<String, Object> data = new HashMap<>();
        System.out.println("token "+token);
        Member member = memberService.getUserInfo(token.getToken());

        if(member.getRole() == Member_Role.GUEST){ //회원 x
            data.put("isOurMember",false);
            data.put("MemberKakaoId",member.getKakaoId());
            return new ResponseDto(HttpStatus.FORBIDDEN.value(), data);
        }
        else{
            data.put("isOurMember",true);
            data.put("MemberKakaoId",member.getKakaoId());

            return new ResponseDto(HttpStatus.OK.value(), data);
        }

    }



    // 회원 가입 완료 버튼 눌렀을 때 (위치 미포함)
    @PutMapping("/sign-up")
    public ResponseDto saveMember(@RequestBody @Valid UnfinishedMemberDto unfinishedMemberDto) throws Exception{
        System.out.println("눌림");
        System.out.println("Access_token"+unfinishedMemberDto.getToken());
        Member member = memberService.getUserInfo(unfinishedMemberDto.getToken());
        Map<String, Object> data = new HashMap<>();

        if(member == null){
            data.put("success", false);
            return new ResponseDto(HttpStatus.FORBIDDEN.value(), data);

        }
        else{
            data.put("success", true);
            memberService.saveMember(member.getKakaoId(), unfinishedMemberDto.getNickname());
            return new ResponseDto(HttpStatus.OK.value(), data);
        }
    }
    @PostMapping("/login/jwt")
    public ResponseDto loginjwt(@RequestBody KakaoDto kakaoDto){
        Map<String, Object> data = new HashMap<>();

        PrincipalDetail principalDetail = (PrincipalDetail) principalDetailService.loadUserByUsername(kakaoDto.getKakaoid());
        data.put("accessToken",jwtTokenUtil.generateToken(principalDetail));
        data.put("refresh",jwtTokenUtil.generateRefreshToken(principalDetail));

        return new ResponseDto(HttpStatus.OK.value(), data);
    }


    // 중복 검사 버튼 눌렀을 때
    @PostMapping("/sign-up/nicknameCheck")
    public ResponseDto nicknameDuplicated(@RequestBody @Valid NicknameDto nicknameDto){
        Map<String, Object> data = new HashMap<>();
        if(memberService.isNicknameDuplicated(nicknameDto.getNickname())){ //중복됨
            data.put("success",false);
            return new ResponseDto(HttpStatus.METHOD_NOT_ALLOWED.value(), data);
        }
        else{
            data.put("success",true);
            return new ResponseDto(HttpStatus.OK.value(), data);

        }
    }
    @PostMapping("/user/me")
    public ResponseDto currentUser(@AuthenticationPrincipal PrincipalDetail userDetails) {
        Map<String, Object> data = new HashMap<>();
        data.put("현재 로그인한 사용자: " , userDetails.getMember());
        return new ResponseDto(HttpStatus.OK.value(), data);
    }

    static class result<T> {
        private T data;
    }    //리프레시 토큰으로 엑세스 토큰 재발급
    //로그아웃

}

