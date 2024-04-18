package backend.time.dto;

import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import backend.time.model.ChatType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChatDto {
    private Long roomId;
    private String writer;
    private String message;
    private ChatType type;
    private LocalDateTime localDateTime = LocalDateTime.now();

    public ChatMessage toEntity(ChatRoom chatRoom){
        ChatMessage chat = ChatMessage.builder()
                .type(this.type)
                .writer(this.writer)
                .message(this.message)
                .build();
        chat.addChat(chatRoom);
        return chat;
    }
}
