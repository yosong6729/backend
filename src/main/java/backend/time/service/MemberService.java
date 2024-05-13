package backend.time.service;


import backend.time.dto.EvaluationDto;
import backend.time.dto.EvaluationResponseDto;
import backend.time.dto.MannerEvaluationDto;
import backend.time.dto.ServiceEvaluationDto;
import backend.time.exception.MemberNotFoundException;
import backend.time.model.Member.*;
import backend.time.model.Objection.Objection;
import backend.time.model.board.Board;
import backend.time.repository.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final EntityManager entityManager;
    private final BoardRepository boardRepository;

    private final MannerEvaluationRepository mannerEvaluationRepository;
    private final ServiceEvaluationRepository serviceEvaluationRepository;
    private final ObjectionRepository objectionRepository;
    private final ObjectionImageRepository objectionImageRepository;

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

    //kakao에게 회원 id 요청
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
            if(isOurMember.isEmpty()){
                return Member.builder()
                        .kakaoId(kakaoId)
                        .role(Member_Role.GUEST)
                        .build();
            }
            else{
                return isOurMember.get();
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

    @Transactional
    public void saveUnfinishMember(String kakaoId){
            Member member = Member.builder()
                    .kakaoId(kakaoId)
                    .role(Member_Role.GUEST)
                    .build();
            memberRepository.save(member);

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
            entityManager.flush();
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

//        objectionImageRepository.deleteAll();
        if(!boardRepository.findByMember(member).isEmpty()){
            System.out.println("yes");
            System.out.println("delete"+boardRepository.findByMember(member).size());
            boardRepository.deleteAll(boardRepository.findByMember(member));
        }
        if(!objectionRepository.findByObjector(member).isEmpty()){
            List<Objection> objectionList = objectionRepository.findByObjector(member);
            for(int i=0; i<objectionList.size(); i++){
                objectionImageRepository.deleteAll(objectionImageRepository.findByObjection(objectionList.get(i)));
                System.out.println("findbyObjector"+objectionRepository.findByObjector(member).get(i));

            }
            objectionRepository.deleteAll(objectionRepository.findByObjector(member));
        }

        memberRepository.delete(isMember);
    }
    @Transactional
    public void sendEvaluation(Long memberId, Long boardId, EvaluationDto evaluationDto){
        Board board = boardRepository.findById(boardId) //내가 평가하기 전에 그 사람이 게시물을 삭제하면...? -> 1. 평가를 못받음 2. 매너 평가만 반영
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 게시물입니다."));
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 회원입니다."));

        // manner 평가 추가
        sendMannerEvaluation(receiver, evaluationDto.mannerEvaluationDtoList);

        //service 평가
        sendServiceEvaluation(receiver, board, evaluationDto.serviceEvaluationDtoList);

    }

    public void sendMannerEvaluation(Member receiver, List<MannerEvaluationCategory> mannerEvaluationCategoryList){
        List<MannerEvaluation> tmpMannerEvaluationList = receiver.getMannerEvaluationList();
        for(int i=0; i<mannerEvaluationCategoryList.size(); i++){
            MannerEvaluationCategory mannerEvaluationCategory = mannerEvaluationCategoryList.get(i);
            Integer isHere = -1;
            for(int j=0; j<tmpMannerEvaluationList.size(); j++){
                if(tmpMannerEvaluationList.get(j).getMannerEvaluationCategory().equals(mannerEvaluationCategory)){
                    isHere = j;
                    break;
                }
            }
            if(isHere != -1){ // 존재하면 => count + 1
                Integer tmpCount = tmpMannerEvaluationList.get(isHere).getMannerEvaluationCount()+1;
                tmpMannerEvaluationList.get(isHere).setMannerEvaluationCount(tmpCount);
            }
            else{ //존재하지 않는 평가면 평가 추가 & count = 1
                MannerEvaluation mannerEvaluation = MannerEvaluation.builder()
                        .member(receiver)
                        .mannerEvaluationCategory(mannerEvaluationCategory)
                        .mannerEvaluationCount(1)
                        .build();
                mannerEvaluationRepository.save(mannerEvaluation);
//                tmpMannerEvaluationList.add(mannerEvaluation);
            }
        }

//        receiver.setMannerEvaluationList(tmpMannerEvaluationList);
    }


    public void sendServiceEvaluation(Member receiver, Board board, List<ServiceEvaluationCategory> serviceEvaluationCategoryList) {
        // board가 없을 경우에는?
        List<ServiceEvaluation> tmpServiceEvaluationList = receiver.getServiceEvaluationList();

        for (int i = 0; i < serviceEvaluationCategoryList.size(); i++) {
            ServiceEvaluationCategory serviceEvaluationCategory = serviceEvaluationCategoryList.get(i);
            Integer isHere = -1;
            for (int j = 0; j < tmpServiceEvaluationList.size(); j++) {
                if (tmpServiceEvaluationList.get(j).getServiceEvaluationCategory().equals(serviceEvaluationCategory)) {
                    isHere = j;
                    break;
                }
            }
            if (isHere != -1) { // 존재하면 => count + 1
                Integer tmpCount = tmpServiceEvaluationList.get(isHere).getServiceEvaluationCount() + 1;
                tmpServiceEvaluationList.get(isHere).setServiceEvaluationCount(tmpCount);
            } else { //존재하지 않는 평가면 평가 추가 & count = 1
                ServiceEvaluation serviceEvaluation = ServiceEvaluation.builder()
                        .member(receiver)
                        .serviceEvaluationCategory(serviceEvaluationCategory)
                        .serviceEvaluationCount(1)
                        .boardCategory(board.getCategory())
                        .build();
                serviceEvaluationRepository.save(serviceEvaluation);
//                tmpServiceEvaluationList.add(serviceEvaluation);
            }
        }
//        receiver.setServiceEvaluationList(tmpServiceEvaluationList);

    }

    public EvaluationResponseDto getEvaluation(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 회원입니다."));
        List<MannerEvaluationDto> mannerEvaluationDtoList = new ArrayList<>();


//        System.out.println("getMannerEvaluationList size : "+member.getMannerEvaluationList().size() );
        for(int i=0; i<member.getMannerEvaluationList().size(); i++){
            MannerEvaluation mannerEvaluation = member.getMannerEvaluationList().get(i);
            MannerEvaluationDto mannerEvaluationDto = MannerEvaluationDto.builder()
                    .mannerEvaluationCategory(mannerEvaluation.getMannerEvaluationCategory())
                    .mannerEvaluationCount(mannerEvaluation.getMannerEvaluationCount())
                    .build();
            mannerEvaluationDtoList.add(mannerEvaluationDto);
        }
        List<ServiceEvaluationDto> serviceEvaluationDtoList = new ArrayList<>();

//        System.out.println("getServiceEvaluationList size : "+member.getServiceEvaluationList().size() );

        for(int i=0; i<member.getServiceEvaluationList().size(); i++){
            ServiceEvaluation serviceEvaluation = member.getServiceEvaluationList().get(i);
            ServiceEvaluationDto serviceEvaluationDto = ServiceEvaluationDto.builder()
                    .serviceEvaluationCategory(serviceEvaluation.getServiceEvaluationCategory())
                    .boardCategory(serviceEvaluation.getBoardCategory())
                    .serviceEvaluationCount(serviceEvaluation.getServiceEvaluationCount())
                    .build();
            serviceEvaluationDtoList.add(serviceEvaluationDto);
        }
        return EvaluationResponseDto.builder()
                .mannerEvaluationList(mannerEvaluationDtoList)
                .serviceEvaluationList(serviceEvaluationDtoList)
                .build();
    }
    //+5점시 매너시간 +5분 -5점시 매너시간 -5분
    // 통합시간, 서비스 시간이 있는데


}

