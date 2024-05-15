package backend.time.repository;

import backend.time.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomId(Long roomId);

    ChatMessage findChatMessageById(Long chatMessageId);
}
