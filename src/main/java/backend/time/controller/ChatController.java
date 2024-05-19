package backend.time.controller;

import backend.time.dto.ChatDto;
import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.Member.Member;
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
        Member member = memberService.findMember(userKakaoId);
        String userNickname = memberService.findMember(userKakaoId).getNickname();
        chatDto.setWriter(userNickname);
        List<ChatMessage> chatList = chattingService.findChatList(chatDto.getRoomId());
        Long roomId = chatDto.getRoomId();
        Optional<ChatRoom> chatRoom = chattingService.findChatRoomById(roomId);
        Board board = chatRoom.get().getBoard();

        //처음 채팅한사람은은 무조건 chatroom의 buyer(채팅누른사람)임
        if (chatList.isEmpty()
                || chatList.get(0).getWriter().equals(member.getNickname())) {
            if (chatRoom.get().getBoard().getBoardType().equals(BoardType.BUY)) {
                log.info("현재 USER = Seller1");
                String UserType = "SELLER";
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
                String buyerId = chatRoom.get().getBoard().getMember().getKakaoId();
                //buyerId(상대방Id) 전달
                notificationService.noReadChatNumberPerChatRoomNotification(userDetails.getUsername(), UserType, savedChat, buyerId);

            } else if (chatRoom.get().getBoard().getBoardType().equals(BoardType.SELL)) { //첫채팅이 유저고 boarType이 SELL일떄
                log.info("현재 USER = BUYER1");
                String UserType = "BUYER";
                String sellerKakaoId = chatRoom.get().getBoard().getMember().getKakaoId();
                if (chatList.isEmpty()) { // 없어도됨
                    chatDto.setMessageId(1L);
                    chatDto.setBuyerRead(1L);
                    chatDto.setSellerRead(0L);
                } else {
                    chatDto.setMessageId(chatList.get(chatList.size() - 1).getMessageId() + 1);
                    chatDto.setBuyerRead(chatList.get(chatList.size() - 1).getMessageId() + 1);
                    chatDto.setSellerRead(chatList.get(chatList.size() - 1).getSellerRead());
                }
                Long savedChat = chattingService.saveChat(chatDto);
                //내가 Buyer이고 상대방 Seller의 KakaoId를 전달
                notificationService.noReadChatNumberPerChatRoomNotification(userDetails.getUsername(), UserType, savedChat, sellerKakaoId);
            }
        } else if (chatRoom.get().getBoard().getBoardType().equals(BoardType.SELL)) {
            log.info("현재 USER = SELLER2");
            String UserType = "SELLER";
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
            String buyerId = chatRoom.get().getBuyer().getKakaoId();
            //buyerId(상대방Id) 전달
            notificationService.noReadChatNumberPerChatRoomNotification(userDetails.getUsername(), UserType, savedChat, buyerId);
        } else if(chatRoom.get().getBoard().getBoardType().equals(BoardType.BUY)){
            log.info("현재 USER = BUYER2");
            String UserType = "BUYER";
            String sellerKakaoId = chatRoom.get().getBuyer().getKakaoId();
            if (chatList.isEmpty()) { // 없어도됨
                chatDto.setMessageId(1L);
                chatDto.setBuyerRead(1L);
                chatDto.setSellerRead(0L);
            } else {
                chatDto.setMessageId(chatList.get(chatList.size() - 1).getMessageId() + 1);
                chatDto.setBuyerRead(chatList.get(chatList.size() - 1).getMessageId() + 1);
                chatDto.setSellerRead(chatList.get(chatList.size() - 1).getSellerRead());
            }
            Long savedChat = chattingService.saveChat(chatDto);
            //내가 Buyer이고 상대방 Seller의 KakaoId를 전달
            notificationService.noReadChatNumberPerChatRoomNotification(userDetails.getUsername(), UserType, savedChat, sellerKakaoId);
        }

        int hour = LocalDateTime.now().getHour();
        int minute = LocalDateTime.now().getMinute();
        String time = hour + ":" + minute;
        chatDto.setTime(time);
        return chatDto;
    }

}