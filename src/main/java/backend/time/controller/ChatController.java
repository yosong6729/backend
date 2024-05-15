package backend.time.controller;

import backend.time.dto.ChatDto;
import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.board.Board;
import backend.time.model.board.BoardType;
import backend.time.service.ChattingService;
import backend.time.service.MemberServiceImpl;
import backend.time.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChattingService chattingService;
    private final MemberServiceImpl memberService;
    private final NotificationService notificationService;

    @PostMapping("/chat/send")
    public ChatDto send(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ChatDto chatDto) {
        //프론트에서 roomId, message, type만 보내면됨
        String userKakaoId = userDetails.getUsername();
        String userNickname = memberService.findMember(userKakaoId).getNickname();
        chatDto.setWriter(userNickname);
        List<ChatMessage> chatList = chattingService.findChatList(chatDto.getRoomId());
        Long roomId = chatDto.getRoomId();
        Optional<ChatRoom> chatRoom = chattingService.findChatRoomById(roomId);
        Board board = chatRoom.get().getBoard();
        //내가 글쓴사람이 아니면 모르는거아닌가 board의 상태와 kakaoid로 판별이 가능한가?

        if (board.getBoardType().equals(BoardType.BUY) && board.getMember().getKakaoId().equals(userKakaoId)) {
            //조건 만족시 BUYER, 아니면 SELLER
            //현재 USER는 BUYER
            //Chatroom의 board의 멤버가 buyer
            String sellerKakaoId = chatRoom.get().getBuyer().getKakaoId();
//            if (chatRoom.get().getBuyer().getKakaoId().equals(userKakaoId)) {
//                sellerKakaoId = chatRoom.get().getBoard().getMember().getKakaoId();
//            } else {
//                sellerKakaoId = userKakaoId;
//            }

            log.info("현재 USER = BUYER");
            if (chatList.isEmpty()) {
                chatDto.setMessageId(1L);
                chatDto.setBuyerRead(1L);
                chatDto.setSellerRead(0L);
            } else {
                chatDto.setMessageId(chatList.get(chatList.size() - 1).getMessageId() + 1);
                chatDto.setBuyerRead(chatList.get(chatList.size() - 1).getMessageId() + 1);
                chatDto.setSellerRead(chatList.get(chatList.size() - 1).getSellerRead());
            }
            Long savedChat = chattingService.saveChat(chatDto);
            //내가 Buyer이고 상대방 Seller의 KakaoId를 넘겨줘야할듯
            notificationService.noReadChatNumberPerChatRoomNotification(savedChat, sellerKakaoId);

        } else {
            //현재 USER는 SELLER
            //buyer KakaoId는?

            log.info("현재 USER = Seller");
            if (chatList.isEmpty()) {
                chatDto.setMessageId(1L);
                chatDto.setBuyerRead(0L);
                chatDto.setSellerRead(1L);
            } else {
                chatDto.setMessageId(chatList.get(chatList.size() - 1).getMessageId() + 1);
                chatDto.setBuyerRead(chatList.get(chatList.size() - 1).getBuyerRead());
                chatDto.setSellerRead(chatList.get(chatList.size() - 1).getMessageId() + 1);
            }
            Long savedChat = chattingService.saveChat(chatDto);
            //buyerId 넘겨주기
            notificationService.noReadChatNumberPerChatRoomNotification(savedChat, userKakaoId);
        }


        int hour = LocalDateTime.now().getHour();
        int minute = LocalDateTime.now().getMinute();
        String time = hour + ":" + minute;
        chatDto.setTime(time);
        return chatDto;
    }

}