package backend.time.chat;

import backend.time.model.ChatRoom;
import backend.time.model.Member;
import backend.time.model.board.Board;
import backend.time.model.board.BoardCategory;
import backend.time.service.BoardService;
import backend.time.service.ChattingService;
import backend.time.service.MemberService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "test2@naver.com", password = "123", roles = "USER")
class ChatRoomControllerTest {

    @Autowired
    ChattingService chattingService;

    @Autowired
    BoardService boardService;

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager em;

    @Autowired
    MockMvc mvc;

    @Autowired
    WebApplicationContext context;

    @BeforeEach
    public void setting(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    public Long createMember(String email){
        Member member = new Member();
        member.setEmail(email);
        member.setNickname(UUID.randomUUID().toString());
        return memberService.join(member);
    }

    public Long createBoard(String email){
        Member seller = memberService.findOne(createMember(email));
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

    /**
     * 채팅방 페이지 ('채팅하기'를 눌렀을 때)
     * 상품 상세페이지에서 넘겨받은 roomName값으로 채팅방을 찾고, 없으면 해당 roomName을 가지는 ChatRoom 객체를 생성하여 DB에 넣어주기
     */
    @Test
    @Transactional
    void enterPage() throws Exception {
        // given
        Long boardId = createBoard("qudgns119@naver.com");
        String userEmail = em.find(Board.class, boardId).getMember().getEmail();
        String roomName = new ChatRoom().getName();
        em.flush();
        // when
        //UserDetails 완성하면 확인
        mvc.perform(get("/chat/room?roomName=" + roomName + "&boardId=" + boardId + "&userEmail=" + userEmail))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }
}