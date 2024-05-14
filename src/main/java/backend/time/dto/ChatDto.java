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
    private String writer;
    private String message;
    private ChatType type;
    private String time;

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
