package backend.time.chat;

import backend.time.dto.ChatDto;
import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.ChatType;
import backend.time.model.Member;
import backend.time.model.board.Board;
import backend.time.model.board.BoardCategory;
import backend.time.service.ChattingService;
import backend.time.service.MemberService;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@Rollback(value = false)
class ChattingServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    ChattingService chattingService;
    @Autowired
    EntityManager em;


    @Test
    @Transactional
    void 채팅_생성(){
        //멤버 select
        Member member = em.find(Member.class, createMember());
        //roomName으로 Chatroom select, 없으면 member, productId, roomName으로 chatroom만들고 가져오기
        ChatRoom room = chattingService.findChatRoomByName(member, "", createBoard());
        //위에서 만든 roomid, 이름,메세지 타입, 메세지로 ChatDto를 DB에 저장후 chatId return
        Long chatId = chattingService.saveChat(ChatDto.builder()
                .roomId(room.getId())
                .writer(member.getNickname())
                .type(ChatType.MESSAGE)
                .message("메시지").build());
        //return 받은 chatId로 chat select
        ChatMessage newChat = em.find(ChatMessage.class, chatId);

        newChat.getChatRoom().getChatMessageList().add(newChat);
        Assertions.assertThat(room.getChatMessageList().size()).isEqualTo(1);
        Assertions.assertThat(room.getLastChat().getMessage()).isEqualTo("메시지");
    }

    @Test
    @Transactional
    public List<ChatRoom> findChatRoomByMember(){
        List<ChatRoom> chatRoomByMember = chattingService.findChatRoomByMember("123");
//        chatRoomByMember.get(0).getBoard().getId();
        return chatRoomByMember;
    }

    public Long createMember(){
        Member member = new Member();
        member.setKakaoId(123L);
        member.setNickname(UUID.randomUUID().toString());
        return memberService.join(member);
    }

    public Long createBoard(){
        Member seller = memberService.findOne(createMember());
        Board board = new Board();
        board.setTitle("상품명");
        board.setBoardCategory(BoardCategory.WAITING);
        board.setItemPrice(100);
        board.setItemTime(60);
        board.setContent("팝니다");
        // 연관관계 편의 메서드 실행
        board.setMember(seller);
        // 상품 DB 저장
        em.persist(board);
        return board.getId();
    }

}