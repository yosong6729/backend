package backend.time.dto;

import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.ChatType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Slf4j
public class ChatDto {
    private Long roomId;
    private Long messageId;
    private String writer;
    private String message;
    private ChatType type;
    private String time;
    private Long buyerRead;
    private Long sellerRead;

    public ChatMessage toEntity(ChatRoom chatRoom) {
        ChatMessage chat = ChatMessage.builder()
                .type(this.type)
                .writer(this.writer)
                .message(this.message)
                .buyerRead(this.buyerRead)
                .sellerRead(this.sellerRead)
                .messageId(this.messageId)
                .build();
        chat.addChat(chatRoom);
        return chat;
    }
}
