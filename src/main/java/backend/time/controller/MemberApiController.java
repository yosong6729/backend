package backend.time.controller;

import backend.time.config.auth.PrincipalDetail;
import backend.time.config.auth.PrincipalDetailService;
import backend.time.config.jwt.JwtTokenUtil;
import backend.time.dto.*;
import backend.time.model.Member;
import backend.time.model.Member_Role;
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

    @GetMapping("/oauth/kakao")
    public ResponseDto ex1(@RequestParam(value = "code") String code){
        System.out.println("token "+code);
        String token = memberService.getReturnAccessToken(code);

        Map<String, Object> data = new HashMap<>();
        data.put("token",token);
        return new ResponseDto(HttpStatus.OK.value(), data);
    }

    //카카오에서 사용자 정보 갖고오기
    @PostMapping("kakao/getinfo")
    public ResponseDto getInfo(@RequestBody TokenDto token) {
        Map<String, Object> data = new HashMap<>();
        System.out.println("token " + token.getToken());
        Member member = memberService.getUserInfo(token.getToken());
        if(member == null){
            data.put("kakaoId",null);
            return new ResponseDto(HttpStatus.FORBIDDEN.value(), data);
        }
        else{
            if(member.getRole().equals(Member_Role.GUEST)) {
                memberService.saveUnfinishMember(member.getKakaoId());
            }
            data.put("kakaoId",member.getKakaoId());
            return new ResponseDto(HttpStatus.OK.value(), data);
        }
    }

    // 닉네임 변경
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

    // 회원 탈퇴
    @DeleteMapping("/delete/member")
    public ResponseDto<Map<String,Boolean>> deleteMember(@AuthenticationPrincipal PrincipalDetail principalDetail){
        memberService.deleteMember(principalDetail.getMember());
        System.out.println("apiController");
        Map<String,Object> data = new HashMap<>();
        data.put("isDelete",true);

        return new ResponseDto(HttpStatus.OK.value(), data);
    }


    // 회원 가입 완료 버튼 눌렀을 때 (위치 미포함)
    @PutMapping("/sign-up")
    public ResponseDto<Map<String,Boolean>> saveMember(@RequestBody @Valid UnfinishedMemberDto unfinishedMemberDto) throws Exception{
//        System.out.println("Id "+unfinishedMemberDto.getKakaoId());
        Map<String, Boolean> data = new HashMap<>();

        boolean isSuccess = memberService.saveMember(unfinishedMemberDto.getKakaoId(), unfinishedMemberDto.getNickname());
        if(isSuccess){
            data.put("success", true);
            return new ResponseDto<>(HttpStatus.OK.value(), data);
        }
        else{
            data.put("success", false);
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), data);
        }

    }

    // 닉네임 중복검사
    @PostMapping("/sign-up/nicknameCheck")
    public ResponseDto<Map<String,Boolean>> nicknameDuplicated(@RequestBody @Valid NicknameDto nicknameDto){
        Map<String, Boolean> data = new HashMap<>();
        System.out.println("nickname"+nicknameDto.getNickname());
        if(memberService.isNicknameDuplicated(nicknameDto.getNickname())){ //중복됨
            data.put("success",false);
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), data);
        }
        else{
            data.put("success",true);
            return new ResponseDto<>(HttpStatus.OK.value(), data);

        }
    }

    //refreshToken 재발급
    @PostMapping("/token/refresh")
    public ResponseDto<Map<String,Object>> refresh(@RequestBody TokenDto tokenDto){
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
                return new ResponseDto<>(HttpStatus.OK.value(), data);
            }
            else{
                data.put("isOurMemeber", false);
                data.put("accessToken",null);
                data.put("refreshToken",null);
                return new ResponseDto<>(HttpStatus.FORBIDDEN.value(),data);
            }
        }

        else{
            data.put("tokenIsExpired", true);
            return new ResponseDto(HttpStatus.FORBIDDEN.value(),data);
        }

    }

    //내 마이페이지
    @GetMapping("/member/profile")
    public Result<MemberDto> getMyProfile(@AuthenticationPrincipal PrincipalDetail principalDetail){
        MemberDto memberDto = memberService.getProfile(principalDetail.getMember().getId());
        return new Result<>(memberDto);
    }
    //타인의 마이페이지
    @GetMapping("/member/{id}/profile")
    public Result<MemberDto> getProfile(@PathVariable Long id){
        MemberDto memberDto = memberService.getProfile(id);
        return new Result<>(memberDto);
    }


    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

}
