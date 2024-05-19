package backend.time.service;

import backend.time.controller.MyPageController;
import backend.time.controller.NotificationController;
import backend.time.dto.*;
import backend.time.model.*;
import backend.time.model.Member.Member;
import backend.time.model.board.Board;
import backend.time.model.board.BoardType;
import backend.time.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final KeywordService keywordService;
    private final MemberService memberService;
    private final BoardService boardService;
    private final ChattingService chattingService;
    private final KeywordNotificationRepository keywordNotificationRepository;
    private final ActivityNotificationRepository activityNotificationRepository;
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
    @Transactional
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
        for (Keyword keyword : allKeyword) {//keyword에 저장된 keyword반복문
            if (title.contains(keyword.getKeyword())) { //keyword가 title에 포함되면
                members_id.add(keyword.getMember().getId());
                if (NotificationController.sseEmitters.containsKey(keyword.getMember().getKakaoId())) { //키워드를 가진 멤버에게보냄
                    SseEmitter sseEmitter = NotificationController.sseEmitters.get(keyword.getMember().getKakaoId());
                    try {
                        eventData.put("keyword", keyword.getKeyword());
                        KeywordNotification entity = KeywordNotification.builder()
                                .member(keyword.getMember()) //키워드를 받은 member
                                .title(title)
                                .keyword(keyword.getKeyword()).build();
                        keywordNotificationRepository.save(entity);
                        sseEmitter.send(SseEmitter.event().name("keywordNotification").data(eventData));
                    } catch (Exception e) {
                        NotificationController.sseEmitters.remove(keyword.getMember().getKakaoId());
                    }
                    eventData.remove("keyword");
                } else {
                    KeywordNotification entity = KeywordNotification.builder()
                            .member(keyword.getMember()) //키워드를 받은 member
                            .title(title)
                            .keyword(keyword.getKeyword()).build();
                    keywordNotificationRepository.save(entity);
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
    @Transactional
    public void notifyScrap(Member member, Long boardId) {
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
                Member receiverMember = memberService.findMember(receiverKakaoId);

                ActivityNotification entity = ActivityNotification.builder()
                        .activityType(ActivityType.SCRAP)
                        .member(receiverMember)
                        .scarpMember(member)
                        .title(title)
                        .nickName(nickname)//스크랩한사람 닉네임
                        .build();

                activityNotificationRepository.save(entity);

                sseEmitter.send(SseEmitter.event().name("scarpNotification").data(eventData));
            } catch (Exception e) {
                NotificationController.sseEmitters.remove(receiverKakaoId);
            }
        } else {
            Member receiverMember = memberService.findMember(receiverKakaoId);

            ActivityNotification entity = ActivityNotification.builder()
                    .activityType(ActivityType.SCRAP)
                    .member(receiverMember)
                    .scarpMember(member)
                    .title(title)
                    .nickName(nickname)//스크랩한사람 닉네임
                    .build();

            activityNotificationRepository.save(entity);

        }
    }

    //거래 완료
    public void transactionComplete(Long roomId){
        log.info("transactionComplete");
        //채팅방에 있는 각자에게 보내면됨
        Optional<ChatRoom> chatRoomById = chattingService.findChatRoomById(roomId);
        String boardKakaoId = chatRoomById.get().getBoard().getMember().getKakaoId();
        String buyerKakaoId = chatRoomById.get().getBuyer().getKakaoId(); //buyer =  게시물 채팅하기 누르사람
        String boardUserNickName = chatRoomById.get().getBoard().getMember().getNickname();
        String buyerUserNickName = chatRoomById.get().getBuyer().getNickname();

        HashMap<String, String> buyerEventData = new HashMap<>();
        buyerEventData.put("traderName", boardUserNickName);
        String time = MyPageController.Time.calculateTime(Timestamp.valueOf(LocalDateTime.now()));
        buyerEventData.put("time", time);


        if (NotificationController.sseEmitters.containsKey(buyerKakaoId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(buyerKakaoId);
            try {
                Member buyerMember = memberService.findMember(buyerKakaoId);

                ActivityNotification entity = ActivityNotification.builder()
                        .activityType(ActivityType.TRANSACTIONCOMPLETE)
                        .member(buyerMember)//알림받는 member
                        .traderName(boardUserNickName)
                        .build();

                activityNotificationRepository.save(entity);

                sseEmitter.send(SseEmitter.event().name("transactionComplete").data(buyerEventData));

            } catch (Exception e) {
                NotificationController.sseEmitters.remove(boardKakaoId);
            }
        } else {
            Member buyerMember = memberService.findMember(buyerKakaoId);

            ActivityNotification entity = ActivityNotification.builder()
                    .activityType(ActivityType.TRANSACTIONCOMPLETE)
                    .member(buyerMember)//알림받는 member
                    .traderName(boardUserNickName)
                    .build();

            activityNotificationRepository.save(entity);
        }

        HashMap<String, String> boardUserEventData = new HashMap<>();
        boardUserEventData.put("traderName", buyerUserNickName);
        boardUserEventData.put("time", time);


        if (NotificationController.sseEmitters.containsKey(boardKakaoId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(boardKakaoId);
            try {
                Member boardMember = memberService.findMember(boardKakaoId);

                ActivityNotification entity = ActivityNotification.builder()
                        .activityType(ActivityType.TRANSACTIONCOMPLETE)
                        .member(boardMember)//알림받는 Member
                        .traderName(buyerUserNickName).build();

                activityNotificationRepository.save(entity);

                sseEmitter.send(SseEmitter.event().name("transactionComplete").data(boardUserEventData));
            } catch (Exception e) {
                NotificationController.sseEmitters.remove(boardKakaoId);
            }
        } else {
            Member boardMember = memberService.findMember(boardKakaoId);

            ActivityNotification entity = ActivityNotification.builder()
                    .activityType(ActivityType.TRANSACTIONCOMPLETE)
                    .member(boardMember)//알림받는 Member
                    .traderName(buyerUserNickName).build();

            activityNotificationRepository.save(entity);
        }
    }

    //알림개수(각 채팅방)
    //매세지를 보낼때 message_id의 값 - buyer_id를 계산(buyer가 안읽은 매세지 수)
    //안읽은 매세지 수 내가 SELLER면 BUYER에게 보내기, 내가 BUYER이면 SELLER에게 보내기
    @Transactional
    public void noReadChatNumberPerChatRoomNotification(String userKakaoId, String userType, Long chatMessageId, String receiverKakaoId) {
        ChatMessage chatMessage = chattingService.findChatMessageById(chatMessageId);
        log.info("chatMessage.getMessageId() = {}", chatMessage.getMessageId());
        Long roomId = chatMessage.getChatRoom().getId();
        long noReadChatNumber;
        Long totalNoReadChat = 0L;
        HashMap<String, Long> eventData = new HashMap<>();
        eventData.put("roomId", roomId);
        //내가속한 채팅방 리스트
        List<ChatRoom> chatRoomList = chattingService.findChatRoomByMember(userKakaoId);
        log.info("chatRoomList = {}", chatRoomList.size());
        //내가 속한 각 채팅방의 내가 BUYER인지 SELLER인지 판단
        for (ChatRoom chatRoom : chatRoomList) {
            if (chatRoom.getBoard().getBoardType().equals(BoardType.BUY) //게시물이 BUY고 게시글 작성자가 나라면 BUYER
                    && chatRoom.getBoard().getMember().getKakaoId().equals(userKakaoId)) {
                try {
                    //내가 BUYER여서 SELLER에게 보냄
                    if (((chatRoom.getLastChat().getMessageId() - chatRoom.getLastChat().getSellerRead()) >= 0)) {
                        totalNoReadChat += (chatRoom.getLastChat().getMessageId() - chatRoom.getLastChat().getSellerRead());
                    }
                } catch (Exception e) {
                    if (chatRoom.getChatMessageList().isEmpty()) {
                        totalNoReadChat += (chatMessage.getMessageId() - chatMessage.getSellerRead());
                    }
                }

            } else if (chatRoom.getBoard().getBoardType().equals(BoardType.BUY) //게시물이 BUY이고 게시글 작성자가 내가 아니면 SELLER
                    && !chatRoom.getBoard().getMember().getKakaoId().equals(userKakaoId)) {
                try {
                    //내가 SELLER여서 BUYER에게 보냄
                    //BUYER가 안읽은 totalNoReadChat 더하기
                    if (((chatRoom.getLastChat().getMessageId() - chatRoom.getLastChat().getBuyerRead()) >= 0)) {
                        totalNoReadChat += (chatRoom.getLastChat().getMessageId() - chatRoom.getLastChat().getBuyerRead());
                    }
                } catch (Exception e) {
                    if (chatRoom.getChatMessageList().isEmpty()) {
                        totalNoReadChat += (chatMessage.getMessageId() - chatMessage.getBuyerRead());
                    }
                }

            } else if (chatRoom.getBoard().getBoardType().equals(BoardType.SELL) //게시물이 SELL이고 게시글 작성자가 나라면 SELLER
                    && chatRoom.getBoard().getMember().getKakaoId().equals(userKakaoId)) {
                try {
                    if (((chatRoom.getLastChat().getMessageId() - chatRoom.getLastChat().getBuyerRead()) >= 0)) {
                        totalNoReadChat += (chatRoom.getLastChat().getMessageId() - chatRoom.getLastChat().getBuyerRead());
                    }
                } catch (Exception e) {
                    if (chatRoom.getChatMessageList().isEmpty()) {
                        totalNoReadChat += (chatMessage.getMessageId() - chatMessage.getBuyerRead());
                    }
                }

            } else if (chatRoom.getBoard().getBoardType().equals(BoardType.SELL) //게시물이 SELL이고 게시물 작성자가 내가 아니면 BUYER
                    && !chatRoom.getBoard().getMember().getKakaoId().equals(userKakaoId)) {
                try {
                    if (((chatRoom.getLastChat().getMessageId() - chatRoom.getLastChat().getSellerRead()) >= 0)) {
                        totalNoReadChat += (chatRoom.getLastChat().getMessageId() - chatRoom.getLastChat().getSellerRead());
                    }
                } catch (Exception e) {
                    if (chatRoom.getChatMessageList().isEmpty()) {
                        totalNoReadChat += (chatMessage.getMessageId() - chatMessage.getSellerRead());
                    }
                }

            }
        }


        //totalChat도 보내야함
        if (userType.equals("SELLER")) {
            //내가 SELLER면 BUYER에게 보내기

//            for (ChatRoom chatRoom : chatRoomList) {
//                if ((chatRoom.getLastChat().getMessageId() - chatRoom.getLastChat().getBuyerRead()) >= 0) {
//                    log.info("chatRoom.getLastChat().getMessageId() = {}", chatRoom.getLastChat().getMessageId());
//                    log.info("chatRoom.getLastChat().getBuyerRead() = {}", chatRoom.getLastChat().getBuyerRead());
//                    totalNoReadChat += (chatRoom.getLastChat().getMessageId() - chatRoom.getLastChat().getBuyerRead());
//                    log.info("totalNoReadChat = {}", totalNoReadChat);
//                }
//            }
            noReadChatNumber = chatMessage.getMessageId() - chatMessage.getBuyerRead();
            eventData.put("noReadChatNumber", noReadChatNumber);
            eventData.put("totalNoReadChat", totalNoReadChat);
            if (NotificationController.sseEmitters.containsKey(receiverKakaoId)) {
                SseEmitter sseEmitter = NotificationController.sseEmitters.get(receiverKakaoId);
                try {
                    sseEmitter.send(SseEmitter.event().name("notReedChatNumberNotification").data(eventData));
                } catch (Exception e) {
                    NotificationController.sseEmitters.remove(receiverKakaoId);
                }
            }
        } else {
            //내가 BUYER이먄 SELLER에게 보내기
            //여기서 내가 무조건 BUYER라고 되어있는데 내가 속해있는 room에는 BUYER일수도 SELLER일수도 있음
//            for (ChatRoom chatRoom : chatRoomList) {
//                log.info("반복문 입장");
//                log.info("chatRoom.getLastChat().getMessageId() = {}", chatRoom.getLastChat().getMessageId());
//                log.info("chatRoom.getLastChat().getSellerRead() = {}", chatRoom.getLastChat().getSellerRead());
//                if ((chatRoom.getLastChat().getMessageId() - chatRoom.getLastChat().getSellerRead()) >= 0) {
//                    totalNoReadChat += (chatRoom.getLastChat().getMessageId() - chatRoom.getLastChat().getSellerRead());
//                    log.info("totalNoReadChat = {}", totalNoReadChat);
//                }
//            }
            noReadChatNumber = chatMessage.getMessageId() - chatMessage.getSellerRead();
            eventData.put("noReadChatNumber", noReadChatNumber);
            eventData.put("totalNoReadChat", totalNoReadChat);
            if (NotificationController.sseEmitters.containsKey(receiverKakaoId)) {
                SseEmitter sseEmitter = NotificationController.sseEmitters.get(receiverKakaoId);
                try {
                    sseEmitter.send(SseEmitter.event().name("notReedChatNumberNotification").data(eventData));
                } catch (Exception e) {
                    NotificationController.sseEmitters.remove(receiverKakaoId);
                }
            }
        }
    }

    //마지막에 있는 채팅의 message_id를 내가 buyer이면 buyer_read에 대입, seller면 seller_read에 대입

    public void whenEnterChatRoomNotificiation(String userType, Long roomId, String userKakaoId) {
//        ChatMessage chatMessage = chattingService.findChatMessageById(chatMessageId);
//        Optional<ChatRoom> chatRoom = chattingService.findChatRoomById(roomId);
//        ChatMessage lastChat = chatRoom.get().getLastChat();
        long noReadChatNumber;
        HashMap<String, Long> eventData = new HashMap<>();
        eventData.put("roomId", roomId);
        //내가 들
        Optional<ChatRoom> chatRoom = chattingService.findChatRoomById(roomId);
        try {
            ChatMessage lastChat = chatRoom.get().getLastChat();
        } catch (Exception e) {
            eventData.put("totalNoReadChat", 0L); //마지막채팅이없으면, 즉 처음 채팅방 입장했을때, 이전totalNoReadChat을 가져와야함
        }

        if (userType.equals("SELLER")) {
            //내가 SELLER면 BUYER에게 보내기

//            noReadChatNumber = chatMessage.getMessageId() - chatMessage.getBuyerRead();

            eventData.put("noReadChatNumber", 0L);
            if (NotificationController.sseEmitters.containsKey(userKakaoId)) {
                SseEmitter sseEmitter = NotificationController.sseEmitters.get(userKakaoId);
                try {
                    sseEmitter.send(SseEmitter.event().name("notReedChatNumberNotification").data(eventData));
                } catch (Exception e) {
                    NotificationController.sseEmitters.remove(userKakaoId);
                }
            }

            chattingService.saveUserTypeReadId("SELLER", roomId);
        } else {
            //내가 BUYER이먄 SELLER에게 보내기
//            noReadChatNumber = chatMessage.getMessageId() - chatMessage.getSellerRead();
//            noReadChatNumber = lastChat.getMessageId() - lastChat.getBuyerRead();
            eventData.put("noReadChatNumber", 0L);
            if (NotificationController.sseEmitters.containsKey(userKakaoId)) {
                SseEmitter sseEmitter = NotificationController.sseEmitters.get(userKakaoId);
                try {
                    sseEmitter.send(SseEmitter.event().name("notReedChatNumberNotification").data(eventData));
                } catch (Exception e) {
                    NotificationController.sseEmitters.remove(userKakaoId);
                }
            }
            chattingService.saveUserTypeReadId("BUYER", roomId);
        }
    }

    //User의 활동알림 리스트 가져오기
    public ActivityNotificationDto activityNotificationList(String userKakaoId) {
        Member member = memberService.findMember(userKakaoId);

        List<ActivityNotification> ACList
                = activityNotificationRepository.findActivityNotificationByMember_IdOrderByCreateDate(member.getId());

        List<ActivityNotificationListDto> collect = ACList.stream().map(m -> {
            ActivityNotificationListDto AN = new ActivityNotificationListDto();
            AN.setActivityId(m.getId());
            AN.setActivityType(m.getActivityType());
            AN.setTitle(m.getTitle());
            AN.setNickName(m.getNickName());
            AN.setTraderName(m.getTraderName());
            AN.setTime(MyPageController.Time.calculateTime(m.getCreateDate()));
            return AN;
        }).collect(Collectors.toList());

        ActivityNotificationDto activityNotificationDto = new ActivityNotificationDto();
        activityNotificationDto.setActivityNotificationListDtoList(collect);

        return activityNotificationDto;
    }

    //User의 키워드 알림 리스트 가져오기
    public KeywordNotificationDto keywordNotificationList(String userKakaoId) {
        Member member = memberService.findMember(userKakaoId);
        Long memberId = member.getId();

        List<KeywordNotification> KNList = keywordNotificationRepository.findAllByMember_Id(memberId);

        List<KeywordNotificationListDto> collect = KNList.stream().map(m -> {
            KeywordNotificationListDto KNLD = new KeywordNotificationListDto();
            KNLD.setKeywordId(m.getId());
            KNLD.setTitle(m.getTitle());
            KNLD.setKeyword(m.getKeyword());
            KNLD.setTime(MyPageController.Time.calculateTime(m.getCreateDate()));

            return KNLD;
        }).collect(Collectors.toList());

        KeywordNotificationDto keywordNotificationDto = new KeywordNotificationDto();
        keywordNotificationDto.setKeywordNotificationListDtos(collect);

        return keywordNotificationDto;
    }

    public void deleteKeywordNotification(Long id) {
        keywordNotificationRepository.deleteById(id);
    }

    public Long findMemberById(Long id) {
        return keywordNotificationRepository.findById(id).get().getMember().getId();
    }

    public Long findActivityNotificationById(Long id) {
        return activityNotificationRepository.findActivityNotificationById(id).getMember().getId();
    }

    public void deleteActivityNotification(Long id) {
        activityNotificationRepository.deleteById(id);
    }







    //댓글 알림
    //파라미터로 게시글 id 가져오기
    //전달받은 boardId로 board select
    //board의 getmember, getid로 member id 가져오기
    //만약 NotificationController.sseEmitters에 memberid가 있다면
    //NotificationController.sseEmitters에 memberid의 key(ssemEmiter)가져오기
    //
}

