package backend.time.controller;

import backend.time.dto.ChatRoomResponseDto;
import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.Member.Member;
import backend.time.service.ChattingService;
import backend.time.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/chat")
@Slf4j
public class ChatRoomControler {

    private final ChattingService chattingService;
    private final MemberService memberService;

    /**
     * 채팅방 페이지 ('채팅하기'를 눌렀을 때)
     * 상품 상세페이지에서 넘겨받은 roomName값으로 채팅방을 찾고, 없으면 해당 roomName을 가지는 ChatRoom 객체를 생성하여 DB에 저장
     */
    @GetMapping("/room")
    public ChatRoomResponseDto enterPage(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("boardId") Long boardId, @RequestParam("roomName") String roomName){
        Member member = memberService.findMember(userDetails.getUsername()); //Member email로 member가져오기
        //이미 roomName이 이미 존재하는 채팅방이 있으면 가져오고 아니면 member(buyer, roomName, productId)를 가지고 새로운 chatroom을 DB에 저장후 가져오기
        ChatRoom room = chattingService.findChatRoomByName(member, roomName, boardId);

        List<ChatMessage> chatList = chattingService.findChatList(room.getId());

        return new ChatRoomResponseDto(room, chatList);
    }


    /**
     * 테스트 용!!!!
     * 채팅방 페이지 ('채팅하기'를 눌렀을 때)
     * 상품 상세페이지에서 넘겨받은 roomName값으로 채팅방을 찾고, 없으면 해당 roomName을 가지는 ChatRoom 객체를 생성하여 DB에 넣어주기
     */
//    @GetMapping("/room")//@AuthenticationPrincipal UserDetails userDetails
//    public ChatRoom enterPage(@RequestParam("userEmail") String userEmail, @RequestParam("boardId") Long boardId, @RequestParam("roomName") String roomName){
//        Member member = memberService.findMember(userEmail); //Member email로 member가져오기
////        Member member = memberService.findMember(userDetails.getUsername());
//        //이미 roomName이 이미 존재하는 채팅방이 있으면 가져오고 아니면 member(buyer, roomName, productId)를 가지고 새로운 chatroom을 DB에 저장후 가져오기
//        ChatRoom room = chattingService.findChatRoomByName(member, roomName, boardId);
//
//        log.info("room = {}", room.toString());
//        return room;
//    }
}
