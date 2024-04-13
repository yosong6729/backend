package backend.time.service;

import backend.time.dto.ResponseDto;
import backend.time.exception.NicknameDuplicatedException;
import backend.time.model.KakaoOauth2UserInfo;
import backend.time.model.Member;
import backend.time.model.Member_Role;
import backend.time.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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

    //액세스 토큰과 리프레시 토큰을 얻기 위함
    public String getReturnAccessToken(String code) {
        System.out.println(code);
        String access_token = "";
        String refresh_token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token"; //토큰 받기
        try {
            System.out.println("1");
            URL url = new URL(reqURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            System.out.println("2");

            //HttpURLConnection 설정 값 셋팅(필수 헤더 세팅)
            con.setRequestMethod("POST"); //인증 토큰 전송
            con.setRequestProperty("Content-type","application/x-www-form-urlencoded"); //인증 토큰 전송
            con.setDoOutput(true); //OutputStream으로 POST 데이터를 넘겨주겠다는 옵션
            System.out.println("3");

            //buffer 스트림 객체 값 셋팅 후 요청
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append("e9bae5955920774f4b427d206bb20954"); // 앱 KEY VALUE
            sb.append("&redirect_uri=").append("http://localhost:8080/oauth/kakao");
            sb.append("&code=" + code);
            bw.write(sb.toString());
            System.out.println("sb="+sb);
            bw.flush();
            con.connect();

            int responseCode = con.getResponseCode();
            String r = con.getResponseMessage();
            System.out.println("responseCode지롱 : "+r);
            System.out.println("responseCode"+responseCode);
            //RETURN 값 result 변수에 저장
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));

            String br_line = "";
            String result = "";

            while ((br_line = br.readLine()) != null) {
                result += br_line;
                System.out.println(result);
            }
            System.out.println("result"+result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            //토큰 값 저장 및 리턴
            access_token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_token = element.getAsJsonObject().get("refresh_token").getAsString();

            br.close();
            bw.close();

        } catch (Exception e) {
            System.out.println("여기...?");
            e.printStackTrace();
        }
        return access_token;
    }

    //사용자 정보 가져오기
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
            // 우리 앱 회원이 아니면 resultMap는 id만 있음

        }
        catch (Exception e){
            System.out.println("사용자 정보를 불러오지 못함");
//            e.printStackTrace();
            return null;
        }
    }

    //회원가입(위치 미포함)
    @Transactional
    public void saveMember(String kakaoId, String nickname){
        Member member = memberRepository.findByKakaoId(kakaoId)
                .orElseThrow(()->new IllegalArgumentException("허용된 토큰이 아닙니다."));
        member.setRole(Member_Role.USER);
        member.setNickname(nickname);

    }


    // 닉네임 중복 검사
    public boolean isNicknameDuplicated(String nickname){
        // 중복 됨
        if(memberRepository.findByNickname(nickname).isPresent()){ return true;}
        // 중복 아닐 때
        else{return false;}
    }




    // 로그아웃 , 성공 시 로그아웃된 사용자 회원번호, 응답코드를 받음 POST
    // 로그아웃 후에 서비스 초기 화면으로 리다이렉트 하는 후속 조치 필요
    // 헤더 : Authorization: Bearer ${Access_token}
    // 요청:서비스 앱 어드민 키 방식..?
    // 헤더 : Authorization : KakaoAK ${SERVICE_APP_ADMIN_KEY}
    // 본문 : target_id_type(String)-> 회원번호 정료, user_id로 고정, target_id(Long)-> 로그 아웃 시킬 사용자 회원번호
    // 응답 : id(Long)로그아웃된 사용자 회원번호
    public void kakaoLogout(){
        String reqURL = "https://kapi.kakao.com/v1/user/logout";
        try{

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    // 회원 탈퇴 : 시스템 회원 탈퇴 후 카카오 연결끊기 api 호출
}