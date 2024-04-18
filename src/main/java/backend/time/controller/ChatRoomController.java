package backend.time.controller;

import backend.time.dto.ChatDto;
import backend.time.dto.ChatRoomResponseDto;
import backend.time.dto.RoomEnterDto;
import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.Member;
import backend.time.service.ChattingService;
import backend.time.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/chat")
@Slf4j
public class ChatRoomController {

    private final ChattingService chattingService;
    private final MemberService memberService;

    /**
     * 채팅방 페이지 ('채팅하기'를 눌렀을 때)
     * 상품 상세페이지에서 넘겨받은 roomName값으로 채팅방을 찾고, 없으면 해당 roomName을 가지는 ChatRoom 객체를 생성하여 DB에 저장
     */
    @GetMapping("/room")
    public ChatRoomResponseDto enterPage(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute RoomEnterDto roomEnterDto){
        Member member = memberService.findMember(userDetails.getUsername()); //Member kakaoId로 member가져오기
//        이미 roomName이 이미 존재하는 채팅방이 있으면 가져오고 아니면 member(buyer, roomName, productId)를 가지고 새로운 chatroom을 DB에 저장후 가져오기
        ChatRoom room = chattingService.findChatRoomByName(member, roomEnterDto.getRoomName(), roomEnterDto.getBoardId());


        List<ChatMessage> chatList = chattingService.findChatList(room.getId());

        List<ChatDto> collect = chatList.stream()
                .map(m -> {ChatDto cm = new ChatDto();
                    cm.setRoomId(m.getChatRoom().getId());
                    cm.setMessage(m.getMessage());
                    cm.setWriter(m.getWriter());
                    cm.setLocalDateTime(m.getCreateDate().toLocalDateTime());
                    return cm;
                }).collect(Collectors.toList());




        ChatRoomResponseDto chatRoomResponseDto = new ChatRoomResponseDto();
        chatRoomResponseDto.setChatlist(collect);

        return chatRoomResponseDto;
    }


    /**
     * 테스트 용!!!!
     * 채팅방 페이지 ('채팅하기'를 눌렀을 때)
     * 상품 상세페이지에서 넘겨받은 roomName값으로 채팅방을 찾고, 없으면 해당 roomName을 가지는 ChatRoom 객체를 생성하여 DB에 넣어주기
     */
//    @GetMapping("/room")
//    public ChatRoomResponseDto enterPage(/*@AuthenticationPrincipal UserDetails userDetails*/ @RequestBody RoomEnterTestDto roomEnterTestDto){
//        Member member = memberService.findMember(roomEnterTestDto.getKakaoId()); //Member kakaoId로 member가져오기
//        log.info("member = {}", member);
//        //이미 roomName이 이미 존재하는 채팅방이 있으면 가져오고 아니면 member(buyer, roomName, productId)를 가지고 새로운 chatroom을 DB에 저장후 가져오기
//        log.info("roomEnterDto.getRoomName() = {}", roomEnterTestDto.getRoomName());
//        log.info("roomEnterDto.getBoardId() = {}", roomEnterTestDto.getBoardId());
//        ChatRoom room = chattingService.findChatRoomByName(member, roomEnterTestDto.getRoomName(), roomEnterTestDto.getBoardId());
//
//
//        List<ChatMessage> chatList = chattingService.findChatList(room.getId());
//
//        List<ChatDto> collect = chatList.stream()
//                .map(m -> {ChatDto cm = new ChatDto();
//                    cm.setRoomId(m.getChatRoom().getId());
//                    cm.setMessage(m.getMessage());
//                    cm.setWriter(m.getWriter());
//                    cm.setLocalDateTime(m.getCreateDate().toLocalDateTime());
//                    return cm;
//                }).collect(Collectors.toList());
//
//
//
//
//        ChatRoomResponseDto chatRoomResponseDto = new ChatRoomResponseDto();
//        chatRoomResponseDto.setChatlist(collect);
//
//        return chatRoomResponseDto;
//    }
}
