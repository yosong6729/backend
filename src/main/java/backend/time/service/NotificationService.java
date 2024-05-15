package backend.time.service;

import backend.time.controller.MyPageController;
import backend.time.controller.NotificationController;
import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.Keyword;
import backend.time.model.Scrap;
import backend.time.model.board.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final KeywordService keywordService;
    private final MemberService memberService;
    private final BoardService boardService;
    private final ChattingService chattingService;
//    private static final Long DEFAULT_TIMEOUT = 60L * 1000;


    public SseEmitter subscribe(String kakaoId){

        // 1. 현재 클라이언트를 위한 sseEmitter 객체 생성
        SseEmitter sseEmitter = new SseEmitter(-1L);

        //만료 시간까지 아무런 데이터를 보내지 않을 경우 발생하는 503에러를 방지하기위해, 더미 데이터 전송
        try {
            sseEmitter.send(SseEmitter.event().name("connect").data("connected!"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //sseemtier에 저장
        NotificationController.sseEmitters.put(kakaoId, sseEmitter);

        // 4. 연결 종료 처리
        sseEmitter.onCompletion(() -> NotificationController.sseEmitters.remove(kakaoId));	// sseEmitter 연결이 완료될 경우
        sseEmitter.onTimeout(() -> NotificationController.sseEmitters.remove(kakaoId));		// sseEmitter 연결에 타임아웃이 발생할 경우
        sseEmitter.onError((e) -> NotificationController.sseEmitters.remove(kakaoId));		// sseEmitter 연결에 오류가 발생할 경우

        return sseEmitter;
    }

    //키워드 알림(제목)
    //게시글 작성하고 db저장후 함수 호출
    public void keywordNotification(Long boardId) {
        //boardId를 파라미터로?
        //member_keyword_list에 있는것들을 하나씩 파라미터로 전달받은 board title에 포함되는지 비교
        //포함되면 member_keyword_list에 있는 member_id에 있는 사람들에게

        Board board = boardService.findOne(boardId);
        String title = board.getTitle();
        Timestamp createDate = board.getCreateDate();
        String time = MyPageController.Time.calculateTime(createDate);
        HashMap<String , String> eventData = new HashMap<>();
        eventData.put("title", title);
        eventData.put("time", time);
        log.info("title = {}", title);
        log.info("time = {}", time);

        List<Keyword> allKeyword = keywordService.findAll();
        List<Long> members_id = new ArrayList<>();
        for (Keyword keyword : allKeyword) {
            if (title.contains(keyword.getKeyword())) {
                members_id.add(keyword.getMember().getId());
                if (NotificationController.sseEmitters.containsKey(keyword.getMember().getKakaoId())) {
                    SseEmitter sseEmitter = NotificationController.sseEmitters.get(keyword.getMember().getKakaoId());
                    try {
                        eventData.put("keyword", keyword.getKeyword());
                        sseEmitter.send(SseEmitter.event().name("keywordNotification").data(eventData));
                    } catch (Exception e) {
                        NotificationController.sseEmitters.remove(keyword.getMember().getKakaoId());
                    }
                    eventData.remove("keyword");
                }
            }
        }
        log.info("member_id = {}", members_id);

    }


    //스크랩 알림
    //파라미터로 게시글 id 가져오기
    //전달받은 boardId로 board select
    //만약 NotificationController.sseEmitters에 memberid가 있다면
    //NotificationController.sseEmitters에 memberid의 key(ssemEmiter)가져오기

    //스크랩 db저장후 호출
    public void notifyScrap(Long boardId) {
        Board board = boardService.findOne(boardId);
        String receiverKakaoId = board.getMember().getKakaoId();

        List<Scrap> scraps = board.getScraps();

        Scrap scrap = scraps.get(scraps.size() - 1);
        String nickname = scrap.getMember().getNickname();
        String title = board.getTitle();
        Timestamp createDate = scrap.getCreateDate();
        String time = MyPageController.Time.calculateTime(createDate);

        HashMap<String , String> eventData = new HashMap<>();
        eventData.put("nickname", nickname);
        eventData.put("title", title);
        eventData.put("time", time);

        if (NotificationController.sseEmitters.containsKey(receiverKakaoId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(receiverKakaoId);
            try {
                sseEmitter.send(SseEmitter.event().name("scarpNotification").data(eventData));
            } catch (Exception e) {
                NotificationController.sseEmitters.remove(receiverKakaoId);
            }
        }
    }

    //거래 완료

    public void transactionComplete(Long roomId){
        //채팅방에 있는 각자에게 보내면됨
        Optional<ChatRoom> chatRoomById = chattingService.findChatRoomById(roomId);
        String boardKakaoId = chatRoomById.get().getBoard().getMember().getKakaoId();
        String buyerKakaoId = chatRoomById.get().getBuyer().getKakaoId(); //buyer =  게시물 채팅하기 누르사람
        String boardUserNickName = chatRoomById.get().getBoard().getMember().getNickname();
        String buyerUserNickName = chatRoomById.get().getBuyer().getNickname();

        HashMap<String, String> buyereventData = new HashMap<>();
        buyereventData.put("traderName", boardUserNickName);
        String time = MyPageController.Time.calculateTime(Timestamp.valueOf(LocalDateTime.now()));
        buyereventData.put("time", time);


        if (NotificationController.sseEmitters.containsKey(buyerKakaoId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(buyerKakaoId);
            try {
                sseEmitter.send(SseEmitter.event().name("transactionComplete").data(buyereventData));
            } catch (Exception e) {
                NotificationController.sseEmitters.remove(boardKakaoId);
            }
        }

        HashMap<String, String> boardUsereventData = new HashMap<>();
        boardUsereventData.put("traderName", buyerUserNickName);
        boardUsereventData.put("time", time);


        if (NotificationController.sseEmitters.containsKey(boardKakaoId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(boardKakaoId);
            try {
                sseEmitter.send(SseEmitter.event().name("transactionComplete").data(boardUsereventData));
            } catch (Exception e) {
                NotificationController.sseEmitters.remove(boardKakaoId);
            }
        }


    }

    //알림개수(각 채팅방)
    //매세지를 보낼때 message_id의 값 - buyer_id를 계산(buyer가 안읽은 매세지 수)
    //안읽은 매세지 수 buyer에게 보내기
    public void noReadChatNumberPerChatRoomNotification(Long chatMessageId, String receiverKakaoId) {
        ChatMessage chatMessage = chattingService.findChatMessageById(chatMessageId);
        Long roomId = chatMessage.getChatRoom().getId();
        long noReadChatNumber = chatMessage.getMessageId() - chatMessage.getSellerRead();

        HashMap<String, Long> eventData = new HashMap<>();
        eventData.put("roomId", roomId);
        eventData.put("noReadChatNumber", noReadChatNumber);
        //BUYER이면? messageId - BuyerRead = 0 전달? 근데 채팅 목록 조회 순간 이미 알림 가는거라면 굳이?
        //걍 SELLER만 줘도 될듯

        if (NotificationController.sseEmitters.containsKey(receiverKakaoId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(receiverKakaoId);
            try {
                sseEmitter.send(SseEmitter.event().name("notReedChatNumberNotification").data(eventData));
            } catch (Exception e) {
                NotificationController.sseEmitters.remove(receiverKakaoId);
            }
        }
    }







    //댓글 알림
    //파라미터로 게시글 id 가져오기
    //전달받은 boardId로 board select
    //board의 getmember, getid로 member id 가져오기
    //만약 NotificationController.sseEmitters에 memberid가 있다면
    //NotificationController.sseEmitters에 memberid의 key(ssemEmiter)가져오기
    //
}

