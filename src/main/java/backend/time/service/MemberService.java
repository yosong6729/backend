package backend.time.service;


import backend.time.exception.MemberNotFoundException;
import backend.time.model.Member;
import backend.time.model.Member_Role;
import backend.time.repository.MemberRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member findMember(String kakaoId) {
        return memberRepository.findByKakaoId(kakaoId).orElseThrow(()->{throw new MemberNotFoundException();});
    }

    @Transactional
    public Long join(Member member) {
        return memberRepository.save(member).getId();
    }

    public Member findOne(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(() -> {throw new MemberNotFoundException();});
    }

    //kakao에게 회원 id 요청
    @Transactional
    public Member getUserInfo(String access_token){
        Map<String, Object> resultMap = new HashMap<>();
        String reqURL = "https://kapi.kakao.com/v2/user/me"; // 사용자 정보 가져오기
        try{
            URL url = new URL(reqURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            //Header 내용
            con.setRequestProperty("Authorization","Bearer "+access_token);

            int responseStatus = con.getResponseCode();
            System.out.println("responseCode : "+ responseStatus);

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String br_line = "";
            String result = "";

            while((br_line = br.readLine())!=null){
                result += br_line;
            }
            System.out.println("result = "+result);

            JsonParser parser = new JsonParser();
            //JsonElement : JSON 데이터의 다양한 요소들에 접근할 수 있게 함
            JsonElement element = parser.parse(result);
//            System.out.println("element = "+element);

            String kakaoId = element.getAsJsonObject().get("id").getAsString();

            Optional<Member> isOurMember = memberRepository.findByKakaoId(kakaoId);
            br.close();

            //존재하면 resultMap 값 넣어줌
            if(isOurMember.isPresent()) {
                return isOurMember.get();

            }
            else{
                Member member = Member.builder()
                        .kakaoId(kakaoId)
                        .role(Member_Role.GUEST)
                        .build();
                memberRepository.save(member);
                return member;
            }

        }
        catch (Exception e){
            System.out.println("kakao로부터 사용자 정보 불러오기 실패");
            return null;
        }
    }

    //회원가입(위치 미포함)
    @Transactional
    public boolean saveMember(String kakaoId, String nickname){
        Optional<Member> member = memberRepository.findByKakaoId(kakaoId);
        if(member.isEmpty()){
            return false;
        }
        else{
            member.get().setRole(Member_Role.USER);
            member.get().setNickname(nickname);
            return true;
        }
    }


    // 닉네임 중복 검사
    public boolean isNicknameDuplicated(String nickname){
        // 중복 됨
        if(memberRepository.findByNickname(nickname).isPresent()){ return true;}
        // 중복 아닐 때
        else{return false;}
    }

    // 닉네임 변경
    @Transactional
    public boolean changeNickname(Member member, String nickname){
        Member isMember = memberRepository.findById(member.getId())
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 회원입니다."));

        if(memberRepository.findByNickname(nickname).isEmpty()){ //닉네임이 중복되지 않으면
            isMember.setNickname(nickname);
            return true;
        }
        else{
            return false;
        }
    }

    //회원 탈퇴 (우리 DB에서만 없애는거)
    @Transactional
    public void deleteMember(Member member){
        Member isMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        memberRepository.delete(isMember);
    }

}

