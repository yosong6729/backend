package backend.time.controller;

import backend.time.dto.ChatDto;
import backend.time.dto.ChatRoomResponseDto;
import backend.time.dto.RoomEnterDto;
import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.Member.Member;
import backend.time.model.board.Board;
import backend.time.model.board.BoardType;
import backend.time.service.BoardServiceImpl;
import backend.time.service.ChattingService;
import backend.time.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/chat")
@Slf4j
public class ChatRoomController {

    private final ChattingService chattingService;
    private final MemberService memberService;
    private final BoardServiceImpl boardService;

    /**
     * 채팅방 페이지 ('채팅하기'를 눌렀을 때)
     * 상품 상세페이지에서 넘겨받은 roomName값으로 채팅방을 찾고, 없으면 해당 roomName을 가지는 ChatRoom 객체를 생성하여 DB에 저장
     */
    @GetMapping("/room")
    public ChatRoomResponseDto enterPage(@AuthenticationPrincipal UserDetails userDetails, @RequestBody RoomEnterDto roomEnterDto){
        Member member = memberService.findMember(userDetails.getUsername()); //Member kakaoId로 member가져오기
        log.info("userKakaoId = {}", userDetails.getUsername());
//        이미 roomName이 이미 존재하는 채팅방이 있으면 가져오고 아니면 member(buyer, roomName, boardId)를 가지고 새로운 chatroom을 DB에 저장후 가져오기
        log.info("roomName = {}", roomEnterDto.getRoomName());
        log.info("boardId = {}", roomEnterDto.getBoardId());
        ChatRoom room = chattingService.findChatRoomByName(member, roomEnterDto.getRoomName(), roomEnterDto.getBoardId());

        Board board = boardService.findOne(roomEnterDto.getBoardId());
        String userKakaoId = userDetails.getUsername();
        ChatRoomResponseDto chatRoomResponseDto = new ChatRoomResponseDto();

        if (board.getBoardType().equals(BoardType.BUY) && board.getMember().getKakaoId().equals(userKakaoId)) {
            //조건 만족시 BUYER, 아니면 SELLER
            chatRoomResponseDto.setRoleType("BUYER");
        }else {
            chatRoomResponseDto.setRoleType("SELLER");
        }

        Long roomId = room.getId();

        List<ChatMessage> chatList = chattingService.findChatList(room.getId());

        List<ChatDto> collect = chatList.stream()
                .map(m -> {ChatDto cm = new ChatDto();
                    cm.setRoomId(m.getChatRoom().getId());
                    cm.setMessage(m.getMessage());
                    cm.setWriter(m.getWriter());
                    cm.setType(m.getType());
//                    cm.setLocalDateTime(m.getCreateDate().toLocalDateTime());
                    return cm;
                }).collect(Collectors.toList());

        chatRoomResponseDto.setChatlist(collect);
        chatRoomResponseDto.setRoomId(roomId);

        return chatRoomResponseDto;
    }

}
