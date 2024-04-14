package backend.time.controller;

import backend.time.model.ChatRoom;
import backend.time.model.Member;
import backend.time.model.board.Board;
import backend.time.service.BoardService;
import backend.time.service.ChattingService;
import backend.time.service.MemberService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final ChattingService chattingService;
    private final MemberService memberService;
    private final BoardService boardService;

    /**
     * 상품 상세 정보 페이지 이동
     * 상품 상세 정보 페이지 이동하자마자 ChatRoom 객체 생성해서 roomName을 포함하여 반환
     */
    @GetMapping("/list/{boardId}")
    public String productDetailPage(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("boardId") Long boardId){
        // 1. 회원 정보 조회
        Member member = memberService.findMember(userDetails.getUsername());
        // 2. 조회할 판매 게시판
        Board board = boardService.findOne(boardId);
        // 3. 구매자, 판매자 채팅방이 없으면 새로 객채생성후 roomName 넘겨주기
        //product_id와 member_id(buyer구매자 아이디)로 채팅방 검색후 없으면 새로운 chatRoom 만들고 Chatroom의 .getName을 해서 roomName가져오기
        String roomName = chattingService.findChatRoomByBuyer(boardId, member.getId()).getName();
//        log.info("roomName = {}", roomName);

        return "";
    }

    /**
     * 상품 상세 정보 페이지 이동(테스트용)
     */
//    @GetMapping("/list/{boardId}")//(@AuthenticationPrincipal UserDetails userDetails
//    public ChatRoom productDetailPage(@RequestParam("userEmail") String userEmail, @PathVariable("boardId") Long boardId) {
//        Member member = memberService.findMember(userEmail);
//
//        Board board = boardService.findOne(boardId);
//
//        ChatRoom findChatRoom = chattingService.findChatRoomByBuyer(boardId, member.getId());
//        String roomName = findChatRoom.getName();
//        log.info("findChatRoom = {}", findChatRoom.toString());
//
//        return findChatRoom;
//    }
}
