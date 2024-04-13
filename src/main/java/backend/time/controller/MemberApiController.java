package backend.time.controller;

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

    @GetMapping("/kakao/login")
    public ResponseDto ex(@RequestParam(value = "token")String accessToken){
        System.out.println("token "+accessToken);
        Map<String, Object> result = memberService.getUserInfo(accessToken);

        Map<String, Object> data = new HashMap<>();
        return new ResponseDto(HttpStatus.OK.value(), data);
    }




    // 회원 가입 완료 버튼 눌렀을 때 (위치 미포함)
    @PostMapping("/sign-up")
    public ResponseDto saveMember(@RequestBody @Valid UnfinishedMemberDto unfinishedMemberDto) throws Exception{
        System.out.println("눌림");
        Map<String, Object> resultMap = memberService.getUserInfo(unfinishedMemberDto.getAccessToken());
        Map<String, Object> data = new HashMap<>();

        if(resultMap == null){
            data.put("success", false);
            return new ResponseDto(HttpStatus.FORBIDDEN.value(), data);

        }
        else{
            data.put("success", true);
            data.put("MemberId", Long.parseLong(String.valueOf(resultMap.get("id"))));
//            memberService.saveMember(Long.parseLong(String.valueOf(resultMap.get("id"))), unfinishedMemberDto.getNickname());

            return new ResponseDto(HttpStatus.OK.value(), data);
        }

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

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    //리프레시 토큰으로 엑세스 토큰 재발급
    //로그아웃

}
