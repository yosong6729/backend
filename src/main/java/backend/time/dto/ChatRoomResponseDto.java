package backend.time.dto;

import backend.time.model.ChatMessage;
import backend.time.model.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatRoomResponseDto {

    ChatRoom room;

    List<ChatMessage> chatlist;

}
