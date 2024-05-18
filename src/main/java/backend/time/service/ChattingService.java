package backend.time.service;


import backend.time.dto.ChatDto;
import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.Member.Member;

import java.util.List;
import java.util.Optional;

public interface ChattingService {
    List<ChatRoom> findChatRoomByMember(String KakaoId);
    ChatRoom findChatRoomByBuyer(Long boardId, Long buyerId);
    Long saveChat(ChatDto chatDto);
    List<ChatMessage> findChatList(Long roomId);
    ChatRoom findChatRoomByName(Member member, String roomName, Long productId);

    Optional<ChatRoom> findChatRoomById(Long roomId);

    ChatMessage findChatMessageById(Long chatMessageId);

    void saveUserTypeReadId(String userType, Long roomId);
}
